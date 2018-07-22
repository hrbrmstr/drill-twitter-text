# drill-twitter-text

An Apache Drill UDF for working with Twitter tweet text via the [`twitter-text`](https://github.com/twitter/twitter-text/tree/master/java) Java library.

## UDFs

- `tw_parse_tweet(string)`: Parses the tweet text and returns a map column with the following named values:
    - `weightedLength`: (int) the overall length of the tweet with code points weighted per the ranges defined in the configuration file
    - `permillage: (int) indicates the proportion (per thousand) of the weighted length in comparison to the max weighted length. A value > 1000 indicates input text that is longer than the allowable maximum.`
    - `isValid`: (boolean) indicates if input text length corresponds to a valid result.
    - `display_start` / `display_end`: (int) indices identifying the inclusive start and exclusive end of the displayable content of the Tweet.
    - `valid_start` / `valid_end`: (int) indices identifying the inclusive start and exclusive end of the valid content of the Tweet.
- `tw_extract_hashtags(string)`: Extracts all hashtags in the tweet text into a list which can be `FLATTEN()`ed.
- `tw_extract_screennames(string)`: Extracts all screennames in the tweet text into a list which can be `FLATTEN()`ed.
- `tw_extract_urls(string)`: Extracts all URLs in the tweet text into a list which can be `FLATTEN()`ed.
- `tw_extract_reply_screenname()`: Extracts the reply screenname (if any) from the tweet text into a `VARCHAR`.

## Building

Retrieve the dependencies and build the UDF:

```
make deps
make udf
```

To automatically install it locally, ensure `DRILL_HOME` is set (the `Makefile` has a default of `/usr/local/drill`) and:

```
make install
```

Assuming you're running in standalone mode, you can then do:

```
make restart
```

You can manually copy:

- `deps/twitter-text-2.0.10.jar`
- `target/drill-twitter-text-1.0.jar`
- `drill-twitter-text-1.0-sources.jar`

(after a successful build) to your `$DRILL_HOME/jars/3rdparty` directory and manually restart Drill as well.

## Example

```
SELECT 
  tw_extract_screennames(tweetText) AS mentions,
  tw_extract_hashtags(tweetText) AS tags,
  tw_extract_urls(tweetText) AS urls,
  tw_extract_reply_screenname(tweetText) AS reply_to,
  tw_parse_tweet(tweetText) AS tweet_meta
FROM
  (SELECT 
     '@youThere Load data from #Apache Drill to @QlikSense - #Qlik Tuesday Tips and Tricks #ApacheDrill #BigData https://t.co/fkAJokKF5O https://t.co/bxdNCiqdrE' AS tweetText
   FROM (VALUES((1))))
```

```
+----------+------+------+----------+------------+
| mentions | tags | urls | reply_to | tweet_meta |
+----------+------+------+----------+------------+
| ["youThere","QlikSense"] | ["Apache","Qlik","ApacheDrill","BigData"] | ["https://t.co/fkAJokKF5O","https://t.co/bxdNCiqdrE"] | youThere | {"weightedLength":154,"permillage":550,"isValid":true,"display_start":0,"display_end":153,"valid_start":0,"valid_end":153} |
+----------+------+------+----------+------------+
```