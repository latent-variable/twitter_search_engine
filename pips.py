import csv
import json
import requests.packages.urllib3    # To disable pip warning msg
import sys  # Used to find the size of a list in bytes
import time
import os
import tweepy

# to disable pip warning message
requests.packages.urllib3.disable_warnings()

#   My keys
consumer_key = 'your own'
consumer_secret = 'your own'
access_token = 'your own'
access_token_secret = 'your own''

# Set access tokens
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

try:
    redirect_url = auth.get_authorization_url()
except tweepy.TweepError:
    print('Error! Failed to get request token.')

# Set up the API instance
api = tweepy.API(auth, wait_on_rate_limit=True, wait_on_rate_limit_notify=True)

# Holds the tweets
tweets = []
tweets.clear()

#   Put some popular twitter user here
username = 'neiltyson'
user = api.get_user(username)


#   Try to print starting user's screen name and follower count
print("user: ", user.screen_name)
print("follower count: ", user.followers_count)
start = True

#   If the program doesn't run after here, the username has protected tweets. Pick a different user in that case
#   print('Starting user has protected tweets. Pick a different starting point ...')

i = 0

#   List of seen usernames. Don't reiterate over them
seen_user = [user]

filename = 'improved_tweets.json'
with open(filename, mode='w') as f:
    #   In the event of a keyboard interrupt, flush everything to the file
    try:
        try:
            while os.stat(filename).st_size < 5000000001:
                for x in seen_user:
                    try:
                        #   Only looks through first 200 friends
                        for friend in tweepy.Cursor(api.friends, id=x.screen_name, count=200).items():
                            #   Print progress every now and then
                            if i % 1000 == 0:
                                print('Size of file: ' + str(os.stat(filename).st_size))

                            #   Ignore users that we have seen already
                            #   This doesn't work for some reason. Not sure why
                            if friend in seen_user:
                                # print('\t\t\t\tSeen this user before. Skipping ...')
                                continue

                            i += 1

                            #   If list gets too big, flush it to the file and clear everything in it
                            #   Avoids inenvitable memory error
                            if len(tweets) > 1:
                                for q in tweets:
                                    try:
                                        f.write(q + '\n')
                                        f.flush()
                                    except UnicodeEncodeError:
                                        continue
                                tweets.clear()

                            #   Add user to seen list
                            seen_user.append(friend)

                            #   Ignore non-geotagged tweets
                            if not friend.geo_enabled:
                                # print('\t\t\t\tGeo-tag not enabled for ' + str(friend.screen_name) + '. Skipping..')
                                continue

                            try:
                                # print('Looking through ' + str(friend.screen_name) + ' tweets')

                                for stat in api.user_timeline(friend.screen_name, count=200):
                                    #   Add the tweets to the list
                                    tweets.append(json.dumps(stat._json))
                                    # try:
                                    #     f.write(stat.text + '\n')
                                    #     f.flush()
                                    # except UnicodeEncodeError:
                                    #     continue

                            except tweepy.TweepError:
                                # print('\t' + str(friend.screen_name) + ' has protected tweets. Skipping ...')
                                pass
                    except tweepy.TweepError:
                        print(str(x.screen_name) + '\'s profile is private. Skipping ...')
        except KeyboardInterrupt:
            for q in tweets:
                try:
                    f.write(q + '\n')
                    f.flush()
                except UnicodeEncodeError:
                    continue
            # print('Keyboard Interrupt. Everything should be written to the file already')

    except MemoryError:
        # Write everything to the file if a memory error occurs
        for q in tweets:
            try:
                f.write(q + '\n')
                f.flush()
            except UnicodeEncodeError:
                continue
        # print('Memory Error. Everything should be written to the file already')

    # Copy tweets to the file 'tweets.txt'
    for q in tweets:
        try:
            f.write(q + '\n')
            f.flush()
        except UnicodeEncodeError:
            continue

# print('Number of retrieved tweets: ' + str(len(tweets)))
print('Number of bytes retrieved: ' + str(os.stat(filename).st_size))

print('\ntweets.txt has 4,368,110 tweets and 35,746,776 bytes')
print('tweets2.txt has 2,407 tweets and 21,048 bytes')
print('tweets3.txt has 337,884 tweets and 3,012,904 bytes')

print('End of program')
