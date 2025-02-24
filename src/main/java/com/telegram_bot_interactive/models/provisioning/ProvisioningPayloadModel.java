package com.telegram_bot_interactive.models.provisioning;

public abstract class ProvisioningPayloadModel {

    public static class OcsPayloadModel {

        public static String OcsSubscriberInfo(String accountId) {
            return """
                {
                    "status": "activate",
                    "service_specification": {
                        "name": "ocs_subscriber_info"
                    },
                    "service_characteristics": [
                        {
                            "name": "account_id",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(accountId);
        }

        public static String queryBundle(String accountId, String bundleId) {
            return """
                {
                	"status": "activate",
                	"service_specification":{
                		"name": "ocs_bundlesubscription_info"
                	},
                	"service_characteristics":[
                        {
                            "name": "account_id",
                            "value": "%s"
                        },
                        {
                            "name": "bundle_id",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(accountId, bundleId);
        }

        public static String queryBundles(String accountId) {
            return """
                {
                	"status": "activate",
                	"service_specification":{
                		"name": "ocs_bundlesubscription_info"
                	},
                	"service_characteristics":[
                        {
                            "name": "account_id",
                            "value": "%s"
                        },
                        {
                            "name": "bundle_id",
                            "value": "ALL"
                        }
                    ]
                }
                """.formatted(accountId);
        }

        public static String BundleSubscription(String accountId, String bundleId, String reason) {
            return """
                {
                	"status": "activate",
                	"service_specification":{
                		"name": "ocs_bundle_subscription"
                	},
                	"service_characteristics":[
                        {
                            "name": "account_id",
                            "value": "%s"
                        },
                        {
                            "name": "bundle_id",
                            "value": "%s"
                        },
                        {
                            "name": "reason",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(accountId, bundleId, reason);
        }

        public static String adjustBalanceDg(String accountId, String dgId, String reason) {
            return """
                {
                	"status": "activate",
                	"service_specification":{
                		"name": "ocs_bundle_subscription"
                	},
                	"service_characteristics":[
                        {
                            "name": "account_id",
                            "value": "%s"
                        },
                        {
                            "name": "dgid",
                            "value": "%s"
                        },
                        {
                            "name": "reason",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(accountId, dgId, reason);
        }

    }

    public static class Hlr {
        public static String querySubscriberInfo(String accountId) {
            return """
                {
                    "status": "activate",
                    "service_specification": {
                        "name": "hlr_info"
                    },
                    "service_characteristics": [
                        {
                            "name": "account_id",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(accountId);
        }

        public static String createAUC(String ki, String imsi) {
            return """
                {
                 	"status": "activate",
                 	"service_specification":{
                 		"name": "auc"
                 	},
                 	"service_characteristics":[
                         {
                             "name": "ki",
                             "value": "%s"
                         },
                         {
                             "name": "imsi",
                             "value": "%s"
                         }
                     ]
                 }
                """.formatted(ki, imsi);
        }

        public  static String createSubscriber(String accountId, String imsi, String profileId) {
            return """
                   {
                   	"status": "activate",
                   	"service_specification":{
                   		"name": "hlr_subscriber"
                   	},
                   	"service_characteristics":[
                           {
                               "name": "imsi",
                               "value": "%s"
                           },
                           {
                               "name": "account_id",
                               "value": "%s"
                           },
                           {
                               "name": "hss_profile",
                               "value": "%s"
                           }
                       ]
                   }
                """.formatted(imsi, accountId, profileId);
        }
    }
}
