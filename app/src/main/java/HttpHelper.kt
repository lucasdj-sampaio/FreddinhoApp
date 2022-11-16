import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request

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
        var dependentList: List<Dependent> = listOf()
        val client = OkHttpClient()
        val mapper = jacksonObjectMapper()

        try {
            val request = Request.Builder()
                    .url("https://freddinho-api.azurewebsites.net/getdependent")
                    .header("userId", userId)
                    .get()
                    .build()
            val response = client.newCall(request).execute()

            response.body()?.string()?.let {
                dependentList = mapper.readValue(it)
            }
        }
        catch (e: Exception){
            println(e.message)
        }

        return dependentList
    }
}