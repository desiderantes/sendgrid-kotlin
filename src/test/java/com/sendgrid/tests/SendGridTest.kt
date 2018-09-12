package com.sendgrid.tests

import com.sendgrid.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

class SendGridTest : Spek({

    given("SendGrid") {
        val SENDGRID_API_KEY = ""

        fun buildDefaultHeaders(): MutableMap<String, String> {
            val sg = SendGrid(SENDGRID_API_KEY)
            val requestHeaders = HashMap<String, String>()
            requestHeaders["Authorization"] = "Bearer $SENDGRID_API_KEY"
            val USER_AGENT = "sendgrid/" + sg.libraryVersion + ";java"
            requestHeaders["User-agent"] = USER_AGENT
            requestHeaders["Accept"] = "application/json"
            return requestHeaders
        }

        on("Initialization") {
            val sg = SendGrid(SENDGRID_API_KEY)
            assertEquals(sg.host, "api.sendgrid.com")
            assertEquals(sg.version, "v3")
            val requestHeaders = buildDefaultHeaders()
            assertEquals(sg.requestHeaders, requestHeaders)
        }

        on("Construct with Client") {
            val client = mock(Client::class.java)
            val sg = SendGrid(SENDGRID_API_KEY, client)
            val request = Request()
            sg.makeCall(request)
            verify(client).api(request)
        }

        on("Library Version") {
            val sg = SendGrid(SENDGRID_API_KEY)
            assertEquals(sg.libraryVersion, "3.0.0")
        }

        on("Version") {
            val sg = SendGrid(SENDGRID_API_KEY)
            sg.version = "v4"
            assertEquals(sg.version, "v4")
        }

        on("Request Headers") {
            val sg = SendGrid(SENDGRID_API_KEY)
            val requestHeaders = buildDefaultHeaders()

            sg.addRequestHeader("Test", "one")
            requestHeaders["Test"] = "one"
            assertEquals(sg.requestHeaders, requestHeaders)

            sg.removeRequestHeader("Test")
            requestHeaders.remove("Test")
            assertEquals(sg.requestHeaders, requestHeaders)
        }

        on("Host") {
            val sg = SendGrid(SENDGRID_API_KEY)
            sg.host = "api.new.com"
            assertEquals(sg.host, "api.new.com")
        }

        on("Rate Limit Retry") {
            val sg = SendGrid(SENDGRID_API_KEY)
            sg.rateLimitRetry = 100
            assertEquals(sg.rateLimitRetry.toLong(), 100)
        }

        on("Rate Limit Sleep") {
            val sg = SendGrid(SENDGRID_API_KEY)
            sg.rateLimitSleep = 999
            assertEquals(sg.rateLimitSleep.toLong(), 999)
        }

        given("Async") {

            on("Operation") {
                val sync = java.lang.Object()
                val isTravis = System.getenv("TRAVIS")?.toBoolean() ?: false
                val sg = if (isTravis) {
                    SendGrid(SENDGRID_API_KEY).apply {
                        this.host = System.getenv("MOCK_HOST")
                    }
                } else {
                    SendGrid(SENDGRID_API_KEY, true).apply {
                        this.host = "localhost:4010"
                    }
                }
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "access_settings/activity"
                    addQueryParam("limit", "1")
                }
                sg.attempt(request, onError = { error: Exception ->
                    fail()
                }, onResponse = { response ->
                    assertEquals(200, response.statusCode.toLong())
                    synchronized(sync) {
                        sync.notify()
                    }
                }
                )

                try {
                    synchronized(sync) {
                        sync.wait(2000)
                    }
                } catch (ex: InterruptedException) {
                    fail(ex.toString())
                }

            }

            on("Rate Limit") {
                val sync = java.lang.Object()
                val isTravis = System.getenv("TRAVIS")?.toBoolean() ?: false
                val sg = if (isTravis) {
                    SendGrid(SENDGRID_API_KEY).apply {
                        this.host = System.getenv("MOCK_HOST")
                    }
                } else {
                    SendGrid(SENDGRID_API_KEY, true).apply {
                        this.host = "localhost:4010"
                    }
                }
                sg.addRequestHeader("X-Mock", "429")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "access_settings/activity"
                    addQueryParam("limit", "1")
                }
                sg.attempt(request, onError = { e: Exception ->
                    assert(e is RateLimitException)
                    sync.notify()
                }, onResponse = { response: Response ->
                    fail()
                })

                try {
                    synchronized(sync) {
                        sync.wait(2000)
                    }
                } catch (ex: InterruptedException) {
                    fail(ex.toString())
                }

            }
        }

        given("access_settings") {

            on("/activity GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    this.method = Method.GET
                    this.endpoint = "access_settings/activity"
                    this.addQueryParam("limit", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/whitelist POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    this.method = Method.POST
                    this.endpoint = "access_settings/whitelist"
                    this.body = "{\"ips\":[{\"ip\":\"192.168.1.1\"},{\"ip\":\"192.*.*.*\"},{\"ip\":\"192.168.1.3/32\"}]}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/whitelist GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "access_settings/whitelist"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/whitelist DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "access_settings/whitelist"
                    body = "{\"ids\":[1,2,3]}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/whitelist/{rule_id}/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "access_settings/whitelist/{rule_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("whitelist/{rule_id}/ DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "access_settings/whitelist/{rule_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }
        }

        given("alerts") {
            on("/ POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "alerts"
                    body = "{\"type\":\"stats_notification\",\"frequency\":\"daily\",\"email_to\":\"example@example.com\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    this.method = Method.GET
                    this.endpoint = "alerts"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{alert_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "alerts/{alert_id}"
                    body = "{\"email_to\":\"example@example.com\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{alert_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "alerts/{alert_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{alert_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "alerts/{alert_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }
        }

        given("api_keys") {
            on("/ POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "api_keys"
                    body = "{\"sample\":\"data\",\"scopes\":[\"mail.send\",\"alerts.create\",\"alerts.read\"],\"name\":\"My API Key\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "api_keys"
                    addQueryParam("limit", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{api_key_id} PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "api_keys/{api_key_id}"
                    body = "{\"scopes\":[\"user.profile.read\",\"user.profile.update\"],\"name\":\"A New Hope\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{api_key_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "api_keys/{api_key_id}"
                    body = "{\"name\":\"A New Hope\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{api_key_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "api_keys/{api_key_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{api_key_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "api_keys/{api_key_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }
        }

        given("asm") {

            given("groups") {

                on("/ POST") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "201")

                    val request = Request().apply {
                        method = Method.POST
                        endpoint = "asm/groups"
                        body = "{\"is_default\":true,\"description\":\"Suggestions for products our users might like.\",\"name\":\"Product Suggestions\"}"
                    }
                    val response = sg.api(request)
                    assertEquals(201, response.statusCode.toLong())
                }

                on("/{group_id} GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "asm/groups"
                        addQueryParam("id", "1")
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/{group_id} PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "201")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "asm/groups/{group_id}"
                        body = "{\"description\":\"Suggestions for items our users might like.\",\"name\":\"Item Suggestions\",\"id\":103}"
                    }
                    val response = sg.api(request)
                    assertEquals(201, response.statusCode.toLong())
                }

                on("/{group_id} GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "asm/groups/{group_id}"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/{group_id} DELETE") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "204")

                    val request = Request().apply {
                        method = Method.DELETE
                        endpoint = "asm/groups/{group_id}"
                    }
                    val response = sg.api(request)
                    assertEquals(204, response.statusCode.toLong())
                }

                on("/{group_id}/}suppressions POST") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "201")

                    val request = Request().apply {
                        method = Method.POST
                        endpoint = "asm/groups/{group_id}/suppressions"
                        body = "{\"recipient_emails\":[\"test1@example.com\",\"test2@example.com\"]}"
                    }
                    val response = sg.api(request)
                    assertEquals(201, response.statusCode.toLong())
                }

                on("/{group_id}/}suppressions GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "asm/groups/{group_id}/suppressions"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/{group_id}/suppressions/search POST") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.POST
                        endpoint = "asm/groups/{group_id}/suppressions/search"
                        body = "{\"recipient_emails\":[\"exists1@example.com\",\"exists2@example.com\",\"doesnotexists@example.com\"]}"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/{group_id}/suppressions/{email} DELETE") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "204")

                    val request = Request().apply {
                        method = Method.DELETE
                        endpoint = "asm/groups/{group_id}/suppressions/{email}"
                    }
                    val response = sg.api(request)
                    assertEquals(204, response.statusCode.toLong())
                }
            }

            given("suppressions") {

                on("/ GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "asm/suppressions"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/global POST") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "201")

                    val request = Request().apply {
                        method = Method.POST
                        endpoint = "asm/suppressions/global"
                        body = "{\"recipient_emails\":[\"test1@example.com\",\"test2@example.com\"]}"
                    }
                    val response = sg.api(request)
                    assertEquals(201, response.statusCode.toLong())
                }

                on("/global/{email} GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "asm/suppressions/global/{email}"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/global/{email} DELETE") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "204")

                    val request = Request().apply {
                        method = Method.DELETE
                        endpoint = "asm/suppressions/global/{email}"
                    }
                    val response = sg.api(request)
                    assertEquals(204, response.statusCode.toLong())
                }

                on("/{email} GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "asm/suppressions/{email}"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }
            }
        }

        on("/browsers_stats GET") {
            val sg = SendGrid(SENDGRID_API_KEY, true)
            sg.host = "localhost:4010"
            sg.addRequestHeader("X-Mock", "200")

            val request = Request().apply {
                method = Method.GET
                endpoint = "browsers/stats"
                addQueryParam("end_date", "2016-04-01")
                addQueryParam("aggregated_by", "day")
                addQueryParam("browsers", "test_string")
                addQueryParam("limit", "test_string")
                addQueryParam("offset", "test_string")
                addQueryParam("start_date", "2016-01-01")
            }
            val response = sg.api(request)
            assertEquals(200, response.statusCode.toLong())
        }

        given("campaigns") {

            on("/ POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "campaigns"
                    body = "{\"custom_unsubscribe_url\":\"\",\"html_content\":\"<html><head><title></title></head><body><p>Check out our spring line!</p></body></html>\",\"list_ids\":[110,124],\"sender_id\":124451,\"subject\":\"New Products for Spring!\",\"plain_content\":\"Check out our spring line!\",\"suppression_group_id\":42,\"title\":\"March Newsletter\",\"segment_ids\":[110],\"categories\":[\"spring line\"],\"ip_pool\":\"marketing\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "campaigns"
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{campaign_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "campaigns/{campaign_id}"
                    body = "{\"html_content\":\"<html><head><title></title></head><body><p>Check out our summer line!</p></body></html>\",\"subject\":\"New Products for Summer!\",\"title\":\"May Newsletter\",\"categories\":[\"summer line\"],\"plain_content\":\"Check out our summer line!\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{campaign_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "campaigns/{campaign_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{campaign_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "campaigns/{campaign_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/{campaign_id}/schedules PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "campaigns/{campaign_id}/schedules"
                    body = "{\"send_at\":1489451436}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{campaign_id}/schedules POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "campaigns/{campaign_id}/schedules"
                    body = "{\"send_at\":1489771528}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/{campaign_id}/schedules GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "campaigns/{campaign_id}/schedules"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{campaign_id}/schedules DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "campaigns/{campaign_id}/schedules"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/{campaign_id}/schedules/now POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "campaigns/{campaign_id}/schedules/now"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/{campaign_id}/schedules/test POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "campaigns/{campaign_id}/schedules/test"
                    body = "{\"to\":\"your.email@example.com\"}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }
        }

        given("categories") {
            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "categories"
                    addQueryParam("category", "test_string")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/stats GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "categories/stats"
                    addQueryParam("end_date", "2016-04-01")
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("categories", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/stats/sums GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "categories/stats/sums"
                    addQueryParam("end_date", "2016-04-01")
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("limit", "1")
                    addQueryParam("sort_by_metric", "test_string")
                    addQueryParam("offset", "1")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("sort_by_direction", "asc")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }

        given("clients") {

            on("/stats GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "clients/stats"
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("end_date", "2016-04-01")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{client_type}/stats GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "clients/{client_type}/stats"
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("end_date", "2016-04-01")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
        given("contactdb") {
            on("/custom_fields POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "contactdb/custom_fields"
                    body = "{\"type\":\"text\",\"name\":\"pet\"}"
                }

                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/custom_fields GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/custom_fields"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/custom_fields/{custom_field_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/custom_fields/{custom_field_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/custom_fields/{custom_field_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "202")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/custom_fields/{custom_field_id}"
                }
                val response = sg.api(request)
                assertEquals(202, response.statusCode.toLong())
            }

            on("/lists POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "contactdb/lists"
                    body = "{\"name\":\"your list name\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/lists GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/lists"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/lists DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/lists"
                    body = "[1,2,3,4]"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/lists/{list_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "contactdb/lists/{list_id}"
                    body = "{\"name\":\"newlistname\"}"
                    addQueryParam("list_id", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/lists/{list_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/lists/{list_id}"
                    addQueryParam("list_id", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/lists/{list_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "202")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/lists/{list_id}"
                    addQueryParam("delete_contacts", "true")
                }
                val response = sg.api(request)
                assertEquals(202, response.statusCode.toLong())
            }

            on("/lists/{list_id}/recipients POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "contactdb/lists/{list_id}/recipients"
                    body = "[\"recipient_id1\",\"recipient_id2\"]"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/lists/{list_id}/recipients GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/lists/{list_id}/recipients"
                    addQueryParam("page", "1")
                    addQueryParam("page_size", "1")
                    addQueryParam("list_id", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/lists/{list_id}/recipients/{recipient_id} POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "contactdb/lists/{list_id}/recipients/{recipient_id}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/lists/{list_id}/recipients{recipient_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/lists/{list_id}/recipients/{recipient_id}"
                    addQueryParam("recipient_id", "1")
                    addQueryParam("list_id", "1")
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/recipients PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "contactdb/recipients"
                    body = "[{\"first_name\":\"Guy\",\"last_name\":\"Jones\",\"email\":\"jones@example.com\"}]"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/recipients POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "contactdb/recipients"
                    body = "[{\"age\":25,\"last_name\":\"User\",\"email\":\"example@example.com\",\"first_name\":\"\"},{\"age\":25,\"last_name\":\"User\",\"email\":\"example2@example.com\",\"first_name\":\"Example\"}]"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/recipients GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/recipients"
                    addQueryParam("page", "1")
                    addQueryParam("page_size", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/recipients DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/recipients"
                    body = "[\"recipient_id1\",\"recipient_id2\"]"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/recipients/billable_count GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/recipients/billable_count"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/recipients/count GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/recipients/count"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/recipients/search GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/recipients/search"
                    addQueryParam("{field_name}", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/recipients/{recipient_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/recipients/{recipient_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/recipients/{recipient_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/recipients/{recipient_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/recipients/{recipient_id}/lists GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/recipients/{recipient_id}/lists"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/reserved_fields GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/reserved_fields"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/segments POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "contactdb/segments"
                    body = "{\"conditions\":[{\"operator\":\"eq\",\"field\":\"last_name\",\"and_or\":\"\",\"value\":\"Miller\"},{\"operator\":\"gt\",\"field\":\"last_clicked\",\"and_or\":\"and\",\"value\":\"01/02/2015\"},{\"operator\":\"eq\",\"field\":\"clicks.campaign_identifier\",\"and_or\":\"or\",\"value\":\"513\"}],\"name\":\"Last Name Miller\",\"list_id\":4}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/segments GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/segments"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/segments/{segment_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "contactdb/segments/{segment_id}"
                    body = "{\"conditions\":[{\"operator\":\"eq\",\"field\":\"last_name\",\"and_or\":\"\",\"value\":\"Miller\"}],\"name\":\"The Millers\",\"list_id\":5}"
                    addQueryParam("segment_id", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/segments/{segment_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/segments/{segment_id}"
                    addQueryParam("segment_id", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/segments/{segment_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "contactdb/segments/{segment_id}"
                    addQueryParam("delete_contacts", "true")
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/segments/{segment_id}/recipients GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "contactdb/segments/{segment_id}/recipients"
                    addQueryParam("page", "1")
                    addQueryParam("page_size", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
        on("/devices/stats GET") {
            val sg = SendGrid(SENDGRID_API_KEY, true)
            sg.host = "localhost:4010"
            sg.addRequestHeader("X-Mock", "200")

            val request = Request().apply {
                method = Method.GET
                endpoint = "devices/stats"
                addQueryParam("aggregated_by", "day")
                addQueryParam("limit", "1")
                addQueryParam("start_date", "2016-01-01")
                addQueryParam("end_date", "2016-04-01")
                addQueryParam("offset", "1")
            }
            val response = sg.api(request)
            assertEquals(200, response.statusCode.toLong())
        }

        on("/geo/stats GET") {
            val sg = SendGrid(SENDGRID_API_KEY, true)
            sg.host = "localhost:4010"
            sg.addRequestHeader("X-Mock", "200")

            val request = Request().apply {
                method = Method.GET
                endpoint = "geo/stats"
                addQueryParam("end_date", "2016-04-01")
                addQueryParam("country", "US")
                addQueryParam("aggregated_by", "day")
                addQueryParam("limit", "1")
                addQueryParam("offset", "1")
                addQueryParam("start_date", "2016-01-01")
            }
            val response = sg.api(request)
            assertEquals(200, response.statusCode.toLong())
        }

        given("ips") {
            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips"
                    addQueryParam("subuser", "test_string")
                    addQueryParam("ip", "test_string")
                    addQueryParam("limit", "1")
                    addQueryParam("exclude_whitelabels", "true")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/assigned GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips/assigned"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/pools POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "ips/pools"
                    body = """{"name":"marketing"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/pools GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips/pools"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/pools/{pool_name} PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "ips/pools/{pool_name}"
                    body = """{"name":"new_pool_name"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/pools/{pool_name} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips/pools/{pool_name}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/pools/{pool_name} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "ips/pools/{pool_name}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/pools/{pool_name}/ips POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "ips/pools/{pool_name}/ips"
                    body = """{"ip":"0.0.0.0"}"""
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/pools/{pool_name}/ips/{ip} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "ips/pools/{pool_name}/ips/{ip}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/warmup POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "ips/warmup"
                    body = """{"ip":"0.0.0.0"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/warmup GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips/warmup"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/warmup/{ip_address} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips/warmup/{ip_address}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/warmup/{ip_address} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "ips/warmup/{ip_address}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/{ip_address} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "ips/{ip_address}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
        given("mail") {

            given("batch") {
                on("/ POST") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "201")

                    val request = Request().apply {
                        method = Method.POST
                        endpoint = "mail/batch"
                    }
                    val response = sg.api(request)
                    assertEquals(201, response.statusCode.toLong())
                }

                on("/{batch_id} GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail/batch/{batch_id}"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }
            }

            on("/send POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "202")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "mail/send"
                    body = """{"custom_args":{"New Argument 1":"New Value 1","activationAttempt":"1","customerAccountNumber":"[CUSTOMER ACCOUNT NUMBER GOES HERE]"},"from":{"email":"sam.smith@example.com","name":"Sam Smith"},"attachments":[{"name":"file1","filename":"file1.jpg","content":"[BASE64 encoded content block here]","disposition":"inline","content_id":"ii_139db99fdb5c3704","type":"jpg"}],"personalizations":[{"to":[{"email":"john.doe@example.com","name":"John Doe"}],"cc":[{"email":"jane.doe@example.com","name":"Jane Doe"}],"bcc":[{"email":"sam.doe@example.com","name":"Sam Doe"}],"custom_args":{"New Argument 1":"New Value 1","activationAttempt":"1","customerAccountNumber":"[CUSTOMER ACCOUNT NUMBER GOES HERE]"},"headers":{"X-Accept-Language":"en","X-Mailer":"MyApp"},"send_at":1409348513,"substitutions":{"type":"object","id":"substitutions"},"subject":"Hello, World!"}],"subject":"Hello, World!","ip_pool_name":"[YOUR POOL NAME GOES HERE]","content":[{"type":"text/html","value":"<html><p>Hello, world!</p><img src=[CID GOES HERE]></img></html>"}],"headers":{},"asm":{"groups_to_display":[1,2,3],"group_id":1},"batch_id":"[YOUR BATCH ID GOES HERE]","tracking_settings":{"subscription_tracking":{"text":"If you would like to unsubscribe and stop receiveing these emails <% click here %>.","enable":true,"html":"If you would like to unsubscribe and stop receiving these emails <% clickhere %>.","substitution_tag":"<%click here%>"},"open_tracking":{"enable":true,"substitution_tag":"%opentrack"},"click_tracking":{"enable":true,"enable_text":true},"ganalytics":{"utm_campaign":"[NAME OF YOUR REFERRER SOURCE]","enable":true,"utm_name":"[NAME OF YOUR CAMPAIGN]","utm_term":"[IDENTIFY PAID KEYWORDS HERE]","utm_content":"[USE THIS SPACE TO DIFFERENTIATE YOUR EMAIL FROM ADS]","utm_medium":"[NAME OF YOUR MARKETING MEDIUM e.g. email]"}},"mail_settings":{"footer":{"text":"Thanks,/n The SendGrid Team","enable":true,"html":"<p>Thanks</br>The SendGrid Team</p>"},"spam_check":{"threshold":3,"post_to_url":"http://example.com/compliance","enable":true},"bypass_list_management":{"enable":true},"sandbox_mode":{"enable":false},"bcc":{"enable":true,"email":"ben.doe@example.com"}},"reply_to":{"email":"sam.smith@example.com","name":"Sam Smith"},"sections":{"section":{":sectionName2":"section 2 text",":sectionName1":"section 1 text"}},"template_id":"[YOUR TEMPLATE ID GOES HERE]","categories":["category1","category2"],"send_at":1409348513}"""
                }
                val response = sg.api(request)
                assertEquals(202, response.statusCode.toLong())
            }

            given("mail_settings") {
                on("/ GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings"
                        addQueryParam("limit", "1")
                        addQueryParam("offset", "1")
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/address_whitelist PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/address_whitelist"
                        body = """{"list":["email1@example.com","example.com"],"enabled":true}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/address_whitelist GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/address_whitelist"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/bcc PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/bcc"
                        body = """{"enabled":false,"email":"email@example.com"}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/bcc GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/bcc"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/bounce_purge PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/bounce_purge"
                        body = """{"hard_bounces":5,"soft_bounces":5,"enabled":true}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/bounce_purge GET") {

                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/bounce_purge"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/footer PATCH") {

                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/footer"
                        body = """{"html_content":"...","enabled":true,"plain_content":"..."}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/footer GET") {

                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/footer"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/forward_bounce PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/forward_bounce"
                        body = """{"enabled":true,"email":"example@example.com"}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/forward_bounce GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/forward_bounce"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/forward_spam PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/forward_spam"
                        body = """{"enabled":false,"email":""}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/forward_spam GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/forward_spam"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/plain_content PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/plain_content"
                        body = """{"enabled":false}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("plain_content GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/plain_content"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/spam_check PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/spam_check"
                        body = """{"url":"url","max_score":5,"enabled":true}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/spam_check GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/spam_check"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/template PATCH") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.PATCH
                        endpoint = "mail_settings/template"
                        body = """{"html_content":"<% body %>","enabled":true}"""
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }

                on("/template GET") {
                    val sg = SendGrid(SENDGRID_API_KEY, true)
                    sg.host = "localhost:4010"
                    sg.addRequestHeader("X-Mock", "200")

                    val request = Request().apply {
                        method = Method.GET
                        endpoint = "mail_settings/template"
                    }
                    val response = sg.api(request)
                    assertEquals(200, response.statusCode.toLong())
                }
            }

            on("/mailbox_providers/stats GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "mailbox_providers/stats"
                    addQueryParam("end_date", "2016-04-01")
                    addQueryParam("mailbox_providers", "test_string")
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                    addQueryParam("start_date", "2016-01-01")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }

        given("partner_settings") {
            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "partner_settings"
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/new_relic PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "partner_settings/new_relic"
                    body = """{"enable_subuser_statistics":true,"enabled":true,"license_key":""}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/new_relic GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "partner_settings/new_relic"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }

        on("/scopes GET") {
            val sg = SendGrid(SENDGRID_API_KEY, true)
            sg.host = "localhost:4010"
            sg.addRequestHeader("X-Mock", "200")

            val request = Request().apply {
                method = Method.GET
                endpoint = "scopes"
            }
            val response = sg.api(request)
            assertEquals(200, response.statusCode.toLong())
        }

        given("senders") {
            on("/ POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "senders"
                    body = """{"city":"Denver","from":{"email":"from@example.com","name":"Example INC"},"zip":"80202","country":"United States","state":"Colorado","address_2":"Apt. 456","address":"123 Elm St.","reply_to":{"email":"replyto@example.com","name":"Example INC"},"nickname":"My Sender ID"}"""
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }

            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "senders"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{sender_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "senders/{sender_id}"
                    body = """{"city":"Denver","from":{"email":"from@example.com","name":"Example INC"},"zip":"80202","country":"United States","state":"Colorado","address_2":"Apt. 456","address":"123 Elm St.","reply_to":{"email":"replyto@example.com","name":"Example INC"},"nickname":"My Sender ID"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{sender_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "senders/{sender_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{sender_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "senders/{sender_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }

            on("/{sender_id}/resend_verification POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "senders/{sender_id}/resend_verification"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }
        }
        on("/stats GET") {
            val sg = SendGrid(SENDGRID_API_KEY, true)
            sg.host = "localhost:4010"
            sg.addRequestHeader("X-Mock", "200")

            val request = Request().apply {
                method = Method.GET
                endpoint = "stats"
                addQueryParam("aggregated_by", "day")
                addQueryParam("limit", "1")
                addQueryParam("start_date", "2016-01-01")
                addQueryParam("end_date", "2016-04-01")
                addQueryParam("offset", "1")
            }
            val response = sg.api(request)
            assertEquals(200, response.statusCode.toLong())
        }

        given("subusers") {

            on("/ POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "subusers"
                    body = """{"username":"John@example.com","ips":["1.1.1.1","2.2.2.2"],"password":"johns_password","email":"John@example.com"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers"
                    addQueryParam("username", "test_string")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/reputations GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers/reputations"
                    addQueryParam("usernames", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/stats GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers/stats"
                    addQueryParam("end_date", "2016-04-01")
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("subusers", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/stats/monthly GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers/stats/monthly"
                    addQueryParam("subuser", "test_string")
                    addQueryParam("limit", "1")
                    addQueryParam("sort_by_metric", "test_string")
                    addQueryParam("offset", "1")
                    addQueryParam("date", "test_string")
                    addQueryParam("sort_by_direction", "asc")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/stats/sums GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers/stats/sums"
                    addQueryParam("end_date", "2016-04-01")
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("limit", "1")
                    addQueryParam("sort_by_metric", "test_string")
                    addQueryParam("offset", "1")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("sort_by_direction", "asc")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }

            on("/{subuser_name} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "subusers/{subuser_name}"
                    body = "{\"disabled\":false}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/{subuser_name} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "subusers/{subuser_name}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/{subuser_name}/ips PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "subusers/{subuser_name}/ips"
                    body = "[\"127.0.0.1\"]"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{subuser_name}/monitor PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "subusers/{subuser_name}/monitor"
                    body = """{"frequency":500,"email":"example@example.com"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{subuser_name}/monitor POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "subusers/{subuser_name}/monitor"
                    body = """{"frequency":50000,"email":"example@example.com"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{subuser_name}/monitor GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers/{subuser_name}/monitor"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{subuser_name}/monitor DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "subusers/{subuser_name}/monitor"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/{subuser_name}/stats/monthly GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "subusers/{subuser_name}/stats/monthly"
                    addQueryParam("date", "test_string")
                    addQueryParam("sort_by_direction", "asc")
                    addQueryParam("limit", "1")
                    addQueryParam("sort_by_metric", "test_string")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
        given("suppression") {

            on("/blocks GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/blocks"
                    addQueryParam("start_time", "1")
                    addQueryParam("limit", "1")
                    addQueryParam("end_time", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/blocks DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/blocks"
                    body = "{\"emails\":[\"example1@example.com\",\"example2@example.com\"],\"delete_all\":false}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/blocks/email GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/blocks/{email}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/blocks/email DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/blocks/{email}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/bounces GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/bounces"
                    addQueryParam("start_time", "1")
                    addQueryParam("end_time", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/bounces DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/bounces"
                    body = "{\"emails\":[\"example@example.com\",\"example2@example.com\"],\"delete_all\":true}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/bounces/email GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/bounces/{email}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/bounces/email DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/bounces/{email}"
                    addQueryParam("email_address", "example@example.com")
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/invalid_emails GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/invalid_emails"
                    addQueryParam("start_time", "1")
                    addQueryParam("limit", "1")
                    addQueryParam("end_time", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/invalid_emails DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/invalid_emails"
                    body = "{\"emails\":[\"example1@example.com\",\"example2@example.com\"],\"delete_all\":false}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/invalid_emails/{email} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/invalid_emails/{email}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/invalid_emails/{email} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/invalid_emails/{email}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/spam_report/{email} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/spam_report/{email}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/spam_report/{email} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/spam_report/{email}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/spam_reports GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/spam_reports"
                    addQueryParam("start_time", "1")
                    addQueryParam("limit", "1")
                    addQueryParam("end_time", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/spam_reports DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "suppression/spam_reports"
                    body = "{\"emails\":[\"example1@example.com\",\"example2@example.com\"],\"delete_all\":false}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/unsubscribes GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "suppression/unsubscribes"
                    addQueryParam("start_time", "1")
                    addQueryParam("limit", "1")
                    addQueryParam("end_time", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }

        given("templates") {

            on("/ POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "templates"
                    body = "{\"name\":\"example_name\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("7 GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "templates"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{template_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "templates/{template_id}"
                    body = "{\"name\":\"new_example_name\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{template_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "templates/{template_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{template_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "templates/{template_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/{template_id}/versions POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "templates/{template_id}/versions"
                    body = """{"name":"example_version_name","html_content":"<%body%>","plain_content":"<%body%>","active":1,"template_id":"ddb96bbc-9b92-425e-8979-99464621b543","subject":"<%subject%>"}"""
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/{template_id}/versions/{version_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "templates/{template_id}/versions/{version_id}"
                    body = """{"active":1,"html_content":"<%body%>","subject":"<%subject%>","name":"updated_example_name","plain_content":"<%body%>"}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{template_id}/versions/{version_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "templates/{template_id}/versions/{version_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/{template_id}/versions/{version_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "templates/{template_id}/versions/{version_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/{template_id}/versions/{version_id}/activate POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "templates/{template_id}/versions/{version_id}/activate"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
        given("tracking_settings") {
            on("/ GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "tracking_settings"
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/click PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "tracking_settings/click"
                    body = "{\"enabled\":true}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/click GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "tracking_settings/click"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/google_analytics PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "tracking_settings/google_analytics"
                    body = "{\"utm_campaign\":\"website\",\"utm_term\":\"\",\"utm_content\":\"\",\"enabled\":true,\"utm_source\":\"sendgrid.com\",\"utm_medium\":\"email\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/google_analytics GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "tracking_settings/google_analytics"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/open PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "tracking_settings/open"
                    body = "{\"enabled\":true}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/open GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "tracking_settings/open"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/subscription PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "tracking_settings/subscription"
                    body = "{\"url\":\"url\",\"html_content\":\"html content\",\"enabled\":true,\"landing\":\"landing page html\",\"replace\":\"replacement tag\",\"plain_content\":\"text content\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/subscription GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "tracking_settings/subscription"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
        given("user") {
            on("/account GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/account"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/credits GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/credits"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/email PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "user/email"
                    body = "{\"email\":\"example@example.com\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/email GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/email"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/password PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "user/password"
                    body = "{\"new_password\":\"new_password\",\"old_password\":\"old_password\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/profile PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "user/profile"
                    body = "{\"city\":\"Orange\",\"first_name\":\"Example\",\"last_name\":\"User\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/profile GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/profile"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/scheduled_sends POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "user/scheduled_sends"
                    body = "{\"batch_id\":\"YOUR_BATCH_ID\",\"status\":\"pause\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/scheduled_sends GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/scheduled_sends"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/scheduled_sends/{batch_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "user/scheduled_sends/{batch_id}"
                    body = "{\"status\":\"pause\"}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/scheduled_sends/{batch_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/scheduled_sends/{batch_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/scheduled_sends/{batch_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "user/scheduled_sends/{batch_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/settings/enforced_tls PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "user/settings/enforced_tls"
                    body = "{\"require_tls\":true,\"require_valid_cert\":false}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/settings/enforced_tls GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/settings/enforced_tls"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/username PUT") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PUT
                    endpoint = "user/username"
                    body = "{\"username\":\"test_username\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/username GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/username"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/webhooks/event/settings PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "user/webhooks/event/settings"
                    body = """{"group_resubscribe":true,"delivered":true,"group_unsubscribe":true,"spam_report":true,"url":"url","enabled":true,"bounce":true,"deferred":true,"unsubscribe":true,"dropped":true,"open":true,"click":true,"processed":true}"""
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/webhooks/event/settings GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/webhooks/event/settings"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/webhooks/event/test POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "user/webhooks/event/test"
                    body = """{"url":"url"}"""
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/webhooks/parse/settings POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "user/webhooks/parse/settings"
                    body = """{"url":"http://email.myhosthame.com","send_raw":false,"hostname":"myhostname.com","spam_check":true}"""
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/webhooks/parse/settings GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/webhooks/parse/settings"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/webhooks/parse/settings/{hostname} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "user/webhooks/parse/settings/{hostname}"
                    body = "{\"url\":\"http://newdomain.com/parse\",\"send_raw\":true,\"spam_check\":false}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/webhooks/parse/settings/{hostname} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/webhooks/parse/settings/{hostname}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/webhooks/parse/settings/{hostname} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "user/webhooks/parse/settings/{hostname}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/webhooks/parse/stats GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "user/webhooks/parse/stats"
                    addQueryParam("aggregated_by", "day")
                    addQueryParam("limit", "test_string")
                    addQueryParam("start_date", "2016-01-01")
                    addQueryParam("end_date", "2016-04-01")
                    addQueryParam("offset", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }

        given("whitelabel") {

            on("/domains POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/domains"
                    body = """{"automatic_security":false,"username":"john@example.com","domain":"example.com","default":true,"custom_spf":true,"ips":["192.168.1.1","192.168.1.2"],"subdomain":"news"}"""
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/domains GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/domains"
                    addQueryParam("username", "test_string")
                    addQueryParam("domain", "test_string")
                    addQueryParam("exclude_subusers", "true")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains/default GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/domains/default"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains/subuser GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/domains/subuser"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains/subuser DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "whitelabel/domains/subuser"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/domains/{domain_id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "whitelabel/domains/{domain_id}"
                    body = "{\"default\":false,\"custom_spf\":true}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains/{domain_id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/domains/{domain_id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains/{domain_id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "whitelabel/domains/{domain_id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/domains/{domain_id}/subuser POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/domains/{domain_id}/subuser"
                    body = "{\"username\":\"jane@example.com\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/domains{id}/ips POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/domains/{id}/ips"
                    body = "{\"ip\":\"192.168.0.1\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains{id}/ips DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "whitelabel/domains/{id}/ips/{ip}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/domains{id}/validate  POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/domains/{id}/validate"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/ips POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/ips"
                    body = "{\"ip\":\"192.168.1.1\",\"domain\":\"example.com\",\"subdomain\":\"email\"}"
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/ips GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/ips"
                    addQueryParam("ip", "test_string")
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/ips/{id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/ips/{id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/ips/{id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "whitelabel/ips/{id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/ips/{id}/validate POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/ips/{id}/validate"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "201")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/links"
                    body = "{\"default\":true,\"domain\":\"example.com\",\"subdomain\":\"mail\"}"
                    addQueryParam("limit", "1")
                    addQueryParam("offset", "1")
                }
                val response = sg.api(request)
                assertEquals(201, response.statusCode.toLong())
            }


            on("/links GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/links"
                    addQueryParam("limit", "1")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links/default GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/links/default"
                    addQueryParam("domain", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links/subuser GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/links/subuser"
                    addQueryParam("username", "test_string")
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links/subuser DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "whitelabel/links/subuser"
                    addQueryParam("username", "test_string")
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/links/{id} PATCH") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.PATCH
                    endpoint = "whitelabel/links/{id}"
                    body = "{\"default\":true}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links/{id} GET") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.GET
                    endpoint = "whitelabel/links/{id}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links/{id} DELETE") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "204")

                val request = Request().apply {
                    method = Method.DELETE
                    endpoint = "whitelabel/links/{id}"
                }
                val response = sg.api(request)
                assertEquals(204, response.statusCode.toLong())
            }


            on("/links/{id}/validate POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/links/{id}/validate"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }


            on("/links/{link_id}/subuser POST") {
                val sg = SendGrid(SENDGRID_API_KEY, true)
                sg.host = "localhost:4010"
                sg.addRequestHeader("X-Mock", "200")

                val request = Request().apply {
                    method = Method.POST
                    endpoint = "whitelabel/links/{link_id}/subuser"
                    body = "{\"username\":\"jane@example.com\"}"
                }
                val response = sg.api(request)
                assertEquals(200, response.statusCode.toLong())
            }
        }
    }
})
