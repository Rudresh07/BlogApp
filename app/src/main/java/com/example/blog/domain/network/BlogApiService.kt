import com.example.blog.domain.data.Blog
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BlogApiService {
    @GET("posts")
    suspend fun getBlogs(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): Response<List<Blog>>

    @GET("posts/{id}")
    suspend fun getBlogById(
        @Path("id") id: Int
    ): Response<Blog>

    companion object {
        private const val BASE_URL = "https://blog.vrid.in/wp-json/wp/v2/"

        fun create(): BlogApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL) // Ensure correct base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BlogApiService::class.java)
        }
    }
}
