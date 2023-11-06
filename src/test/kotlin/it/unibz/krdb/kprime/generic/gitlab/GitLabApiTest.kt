package it.unibz.krdb.kprime.generic.gitlab

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.junit.Ignore
import org.junit.Test

class GitLabApiTest {

    @JacksonXmlRootElement()
    data class userApi(var name:String? = "")

    @Test
    @Ignore
    fun test_user_api() {
        // given
        val json = "{\"id\":742,\"name\":\"Pedot Nicola\",\"username\":\"Nicola.Pedot\",\"state\":\"active\",\"avatar_url\":\"https://secure.gravatar.com/avatar/0139e020a08f1d3bef1643f61cb2f4f1?s=80\\u0026d=identicon\",\"web_url\":\"https://gitlab.inf.unibz.it/Nicola.Pedot\",\"created_at\":\"2019-03-07T09:24:04.189+01:00\",\"bio\":null,\"location\":null,\"public_email\":\"\",\"skype\":\"\",\"linkedin\":\"\",\"twitter\":\"\",\"website_url\":\"\",\"organization\":null,\"job_title\":\"\",\"last_sign_in_at\":\"2020-04-07T08:28:58.673+02:00\",\"confirmed_at\":\"2019-03-07T09:24:04.162+01:00\",\"last_activity_on\":\"2020-04-30\",\"email\":\"nicola.pedot@unibz.it\",\"theme_id\":1,\"color_scheme_id\":1,\"projects_limit\":100,\"current_sign_in_at\":\"2020-05-06T16:53:05.880+02:00\",\"identities\":[{\"provider\":\"saml\",\"extern_uid\":\"uX25olIsMkuvv5byb2jU7g==\"}],\"can_create_group\":true,\"can_create_project\":true,\"two_factor_enabled\":false,\"external\":false,\"private_profile\":false}"
        val json2 = "{\"name\":\"Pedot Nicola\"}"
        // when
        val readValue = ObjectMapper().readValue(json2, userApi::class.java)
        val writeValue= ObjectMapper().writeValueAsString(userApi("Ciao"))
            val readTree = ObjectMapper().readTree(json2)
        // then
        //assertEquals("742",readValue.name)
        println(writeValue)
        println(json2)
        println(readValue.name)
        println(readTree["name"])
    }
}