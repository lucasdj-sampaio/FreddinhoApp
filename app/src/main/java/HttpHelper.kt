import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HttpHelper {

    fun getUserId(email: String, password: String): String{
        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://freddinho-api.azurewebsites.net/validcredential")
                .header("password", password)
                .header("email", email)
                .build()

        val exec = client.newCall(request).execute();

        return exec.body()?.string() as String
    }

    fun getDependentByUserId(userId: String): List<Dependent>{
        val dependentList: List<Dependent>
        val client = OkHttpClient()
        val mapper = jacksonObjectMapper()

        try {
            val request = Request.Builder()
                    .url("https://freddinho-api.azurewebsites.net/getdependent")
                    .header("userId", userId)
                    .get()
                    .build()
            val response = client.newCall(request).execute()

            dependentList = mapper.readValue(response.body()?.string()
                    , List::class.java) as List<Dependent>
        }
        catch (e: Exception){
            println(e.message)
            return listOf()
        }

        return dependentList
    }
}