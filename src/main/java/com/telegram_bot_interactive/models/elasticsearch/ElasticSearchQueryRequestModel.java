package com.telegram_bot_interactive.models.elasticsearch;


import com.telegram_bot_interactive.common.enums.DatetimeUnit;
import com.telegram_bot_interactive.common.wrappers.DateTimeWrapper;

import java.util.StringJoiner;

public abstract class ElasticSearchQueryRequestModel {

    public static String buildQueryAggregate(int numberOfDay) {

        var queryAggregate = """
            {
                {QUERY},
                {AGGREGATE}
            }
            """;

        final String dateFormat = "yyyy-MM-dd";
        var lastNDays = numberOfDay - 1;
        var query = query(lastNDays);
        var aggregate = aggregate();

        var jsonMultiDays = new StringJoiner(",");
        var j = 0;
        var date = DateTimeWrapper.now("yyyy-MM-dd");
        for (var i = 0; i <= lastNDays; ++i) {
            if (i > 1 ) {
                ++j;
            }
            var d = DateTimeWrapper.addDate(DateTimeWrapper.now(), -i, DatetimeUnit.DAY);
            date = DateTimeWrapper.toString(d, dateFormat);
            jsonMultiDays.add(aggregateDateTemplate(date, i, j));
        }

        queryAggregate = queryAggregate
            .replace("{QUERY}", query)
            .replace("{AGGREGATE}", aggregate.replace("{MULTI_DAYS_JSON}", jsonMultiDays.toString()));

        return queryAggregate;
    }

    public static String query(int numberOfDays) {
        return """
            "size": 0,
            "query": {
                "bool": {
                  "must": [
                    {
                      "terms": {
                        "error_message.keyword": ["topup and successfully activated plan","successfully activated plan"]
                      }
                    }
                  ],
                  "filter": [
                    {
                      "range": {
                        "@timestamp": {
                          "gte": "now-%sd/d",
                          "lte": "now/d",
                          "time_zone": "+07:00"
                        }
                      }
                    }
                  ]
                }
            }
        """.formatted(numberOfDays);
    }

    public static String aggregate() {
        return """
            "aggs": {
                    "by_time": {
                        "date_histogram": {
                            "field": "@timestamp",
                            "calendar_interval": "hour"
                        },
                        "aggs": {
                            "count": {
                                "value_count": {
                                    "field": "@timestamp"
                                }
                            },
                            {MULTI_DAYS_JSON}
                        }
                    }
                }
            """;
    }

    private static String aggregateDateTemplate(String date, int gte, int lte) {
        return """
            "%s": {
                "filter": {
                    "range": {
                        "@timestamp": {
                            "gte": "now-%sd/d", // start-of-today : now-(j)d
                            "lte": "now-%sd", // end-of-the-day, because it include time: now-(k)d
                            "time_zone": "+07:00"
                        }
                    }
                }
            }
            """.formatted(date, gte, lte);
    }
}
