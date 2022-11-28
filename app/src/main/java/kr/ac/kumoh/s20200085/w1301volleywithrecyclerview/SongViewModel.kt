package kr.ac.kumoh.s20200085.w1301volleywithrecyclerview

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

//파스칼 케이스
class SongViewModel(application: Application) : AndroidViewModel(application) {
    data class Song (var id: Int, var title: String, var singer: String)
    //서버에 있는 sql에 맞춰준다.

    companion object{
        const val Queue_TAG = "SongVolleyRequest"
    }

    //진짜 사용하려고 arraylist로 가지고 있음
    private val songs = ArrayList<Song>()
    private val _list = MutableLiveData<ArrayList<Song>>()  //객체가 만들어짐
    val list: LiveData<ArrayList<Song>> //타입을 livedata로
        get() = _list

    private val queue: RequestQueue

    init {
        _list.value = songs
        queue = Volley.newRequestQueue(getApplication())
    }

    fun requestSong(){
        //[]:JsonArrayRequest
        val request = JsonArrayRequest(
            Request.Method.GET,
            "https://expresssongdb-numww.run.goorm.io/song",
            null,
            {//뷰모델에서 뷰를 건드리는 건 좋지 않다
//                Toast.makeText(getApplication(),
//                it.toString(),
//                Toast.LENGTH_LONG).show()
                songs.clear()
                parseJson(it)
                _list.value = songs //반영(중요)
            },
            {
                Toast.makeText(getApplication(),
                    it.toString(),
                    Toast.LENGTH_LONG).show()
            }
        )
        request.tag = Queue_TAG
        queue.add(request)
    }
    
    private fun parseJson(items: JSONArray){
        for(i in 0 until items.length()){
            val item = items[i] as JSONObject
            val id = item.getInt("id")
            val title = item.getString("title")
            val singer = item.getString("title")

            songs.add(Song(id, title, singer))
        }
    }
    override fun onCleared(){
        super.onCleared()
        queue.cancelAll(Queue_TAG)
    }
}

