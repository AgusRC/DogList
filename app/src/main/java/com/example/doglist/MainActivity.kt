package com.example.doglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doglist.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main) //quitamos esto y...
        //...Usamos View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svDogs.setOnQueryTextListener(this)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        adapter = DogAdapter(dogImages)
        binding.rvDogs.layoutManager = LinearLayoutManager(this)
        binding.rvDogs.adapter = adapter
    }

    private fun getRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //CoroutineScope ejecuta en un hilo asincrono para no bloquear el MainActivity
    private fun searchByName(query:String){
        CoroutineScope(Dispatchers.IO).launch {
            //llamada a la interfaz de APIservice,   getDogsByBreeds recibe el string de la raza
            val call = getRetrofit().create(APIService::class.java).getDogsByBreeds("$query/images")

            //Recuperar objeto real de Response
            val puppies = call.body()

            runOnUiThread(){    //vuelve al hilo principal
                if(call.isSuccessful){
                    //si la consulta tuvo exito
                    //show Recyclerview
                    // ?: si es nulo crea una lista nula
                    val images = puppies?.images ?: emptyList()
                    //Mandar al adapter
                    dogImages.clear()
                    dogImages.addAll(images)
                    //decirle al adapter que hubo cambios
                    adapter.notifyDataSetChanged()

                }else{
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Ha ocurrido un error!", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()){
            searchByName(query.toLowerCase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
            return true
    }
}