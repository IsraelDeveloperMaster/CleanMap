package net.developermaster.cleanmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.firebase.firestore.FirebaseFirestore
import net.developermaster.cleanmap.databinding.ActivityMainBinding
import net.developermaster.cleanmap.model.dataClassLatitudeLongitude

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener /*, OnMyLocationClickListener*/ {

    //todo firebase
    private lateinit var database: FirebaseFirestore
    private lateinit var dataClassLatitudeLongitud: dataClassLatitudeLongitude

    //////// GOOGLE MAP ////////

    private var marcadorNovo: LatLng? = null

    //todo model de lugares
//    private lateinit var modelLugares: ModelLugares

    //todo variavel do googleMap
    private lateinit var googleMap: GoogleMap

    //todo desenho de linha ( Dot() = Ponto, Gap() = Espaço, Dash() = Linha)
    val pattern = listOf(
        Dot(), Gap(10F), Dash(50F), Gap(10F)
    )

    //todo verifica se tem permissao para acessar a localização
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    //todo configuracao de googleMap aqui
    override fun onMapReady(googleMap: GoogleMap) {

        /*
        todo this.googleMap para googleMap / agora nao precisa declarar:
         exemplo this.googleMap.isBuildingsEnabled = true
         agora sera assim
         googleMap.isBuildingsEnabled = true
        */
        this.googleMap = googleMap

        //todo edifícios 3D
//        googleMap.isBuildingsEnabled = true

        //todo habilitar mapas internos
//        googleMap.isIndoorEnabled = true

        //todo rota do mapa
//        googleMap.isTrafficEnabled = true

        //todo habilitar os controles de zoom
//        googleMap.uiSettings.isZoomGesturesEnabled = true

        //todo habilitar controles de zoom
//        googleMap.uiSettings.isZoomControlsEnabled = true

        //todo Ativa ou desativa a bússola
//        googleMap.uiSettings.isCompassEnabled = true

        //todo Ativa ou desativa a butao de localização
//        googleMap.uiSettings.isMyLocationButtonEnabled = true

        //todo implementacao do metodo OnMyLocationButtonClickListener
//        googleMap.setOnMyLocationButtonClickListener(this)

        //todo implementacao do metodo OnMyLocationClickListener
//        googleMap.setOnMyLocationClickListener(this)

        //todo Ativa ou desativa a barra de ferramentas
//        googleMap.uiSettings.isMapToolbarEnabled = true

        //todo Adiciona um marcador no mapa com um clique
        googleMap.setOnMapClickListener { latitudeLongitude ->

//            criandoMarcadoresNovo(latitudeLongitude)

//            criandoMarcadoresComFirebase( latitudeLongitude )

            criandoMarcadoresSalvaFirebase(latitudeLongitude)

        }

        criandoMarcadores()

        listandoMarcadoresSalvosFirebase()

        habilitarBotaoMinhaLocalizacao()
    }

    private fun criandoMarcadoresSalvaFirebase(latitudeLongitude: LatLng) {

        //todo Armazena a posição do marcador
        marcadorNovo = latitudeLongitude

        googleMap.addMarker(
            MarkerOptions().position(marcadorNovo!!).title("Novo marcador")
                .snippet("Descrição do marcador")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_architecture))
        )

        //todo salva no firebase
        FirebaseFirestore.getInstance().collection("CleanMap").document().set(marcadorNovo!!)

        Toast.makeText(this, "Novo marcador firebase criado", Toast.LENGTH_SHORT).show()

    }

    private fun listandoMarcadoresSalvosFirebase() {

        var listaDeMarcadoresResultado = ""

        val listaDeMarcadoresRetornados =
            FirebaseFirestore.getInstance().collection("CleanMap")//todo collection

        listaDeMarcadoresRetornados.addSnapshotListener { dadosRetornados, error ->

            val marcadoresRetornados = dadosRetornados?.documents//todo document

            marcadoresRetornados?.forEach { documents ->

                val dados = documents?.data //todo dados do documento retornado

                if (dados != null) {

                    val id = documents.id
                    val latitude = dados["latitude"]
                    val longitude = dados["longitude"]

                    listaDeMarcadoresResultado += (" id: ${id} \n latitude: ${latitude} \n longitude: ${longitude} \n \n ")
                    Log.d("firebase", "listandoMarcadoresSalvosFirebase: " + listaDeMarcadoresResultado)

                    val marcadoresFavoritos = LatLng(latitude as Double, longitude as Double)

                    //todo marcador
//                    val lugarFavorito = LatLng(61.044195, -18.5363842)

                    googleMap
                        .addMarker( MarkerOptions()
                            .position(marcadoresFavoritos).title("Mi playa favorita!")
                            .snippet("Ilhas Canarias")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_architecture))
                        )

                    //todo animacao da camera
                    googleMap
                        .animateCamera(CameraUpdateFactory.newLatLngZoom(marcadoresFavoritos, 30f), 2000, null)
                }
            }
        }
    }


    private fun funcaoListarTodos() {

        //todo lista de resultados
        var listaResultado = ""

        val listaDeDadosRetornadas =
            FirebaseFirestore.getInstance().collection("FireBaseSimples")//todo collection

        listaDeDadosRetornadas.addSnapshotListener { dadosRetornados, error ->

            val listaRetornada = dadosRetornados?.documents//todo document

            listaRetornada?.forEach { documents ->

                val dados = documents?.data //todo dados do documento retornado

                if (dados != null) {

                    val id = documents.id
                    val nome = dados["nome"]
                    val idade = dados["idade"]

                    listaResultado += (" id: ${id} \n Nome: ${nome} \n idade: ${idade} \n \n ")

//                    binding.textView.text = listaResultado

//                    funcaoListarImage()

//                    funcaoLimpaCampos()
                }
            }
        }
    }

    private fun criandoMarcadoresNovo(latitudeLongitude: LatLng) {

        // Armazena a posição do marcador
        marcadorNovo = latitudeLongitude

        googleMap.addMarker(
            MarkerOptions().position(latitudeLongitude).title("Novo marcador")
                .snippet("Descrição do marcador")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_architecture))
        )

        Toast.makeText(this, "Novo marcador criado", Toast.LENGTH_SHORT).show()
    }

    /*
        private fun criandoMarcadoresNovoComFirebase(latitudeLongitude: LatLng) {

            FirebaseDatabase.getInstance()
    //        val myRef = database.getReference("CleanMap")

            // Armazena a posição do marcador
            marcadorNovo = latitudeLongitude

            // Salvar no Firebase
            val marcador = googleMap.addMarker( MarkerOptions().position(latitudeLongitude).title("Novo marcador")
                .snippet("Descrição do marcador")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_architecture)) )

            if (marcador != null) {
    //            myRef.push().setValue(marcador)
            }

            Toast.makeText(this, "Novo marcador criado", Toast.LENGTH_SHORT).show()
        }
    */

    private fun criandoDesenhos() {
        val polylineOptions = PolylineOptions()
            //todo linha
            .add(LatLng(40.419173113350965, -3.705976009368897))
            .add(LatLng(40.4150807746539, -3.706072568893432))
            .add(LatLng(40.41517062907432, -3.7012016773223873))
            .add(LatLng(40.41713105928677, -3.7037122249603267))
            .add(LatLng(40.41926296230622, -3.701287508010864))
            .add(LatLng(40.419173113350965, -3.7048280239105225))

            //todo fechamento da linha
//            .add(LatLng(40.419173113350965, -3.705976009368897))

            //todo espessura da linhavvvf
            .width(30f)

            //todo cor da linha
            .color(ContextCompat.getColor(this, R.color.black))

        val polyline = googleMap.addPolyline(polylineOptions)

//todo cantos da linha
        polyline.startCap = RoundCap()

//todo figura no final da linha
        polyline.endCap =
            CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_marcador), 500f)

//todo linhas pontilhadas
        polyline.pattern = pattern

//todo polylines clicáveis
        polyline.isClickable = true

//todo polylines clicáveis
        googleMap.setOnPolylineClickListener {

            mundaCorPolylineRandom(polyline)

            Toast.makeText(this, "Linha clicada", Toast.LENGTH_SHORT).show()
        }
    }

    //todo cria os marcadores fixos
    private fun criandoMarcadores() {

        //todo marcador
        val lugarFavorito = LatLng(28.044195, -16.5363842)

        googleMap.addMarker(
            MarkerOptions().position(lugarFavorito).title("Mi playa favorita!")
                .snippet("Ilhas Canarias")
        )

        //todo animacao da camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lugarFavorito, 30f), 2000, null)
    }

    /*

        private fun criandoMarcadoresComFirebase(latitudeLongitude: LatLng) {

            database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("CleanMap")

            //todo marcador
            val lugarFavorito = LatLng(28.044195, -16.5363842)

            // Salvar no Firebase
            val marcador = googleMap.addMarker( MarkerOptions().position(lugarFavorito).title("Mi playa favorita!") .snippet("Ilhas Canarias") )

            if (marcador != null) {
                myRef.push().setValue(marcador)
            }

    //        googleMap.addMarker( MarkerOptions().position(lugarFavorito).title("Mi playa favorita!") .snippet("Ilhas Canarias") )

            //todo animacao da camera
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lugarFavorito, 20f), 4000, null)



    *//*
            FirebaseDatabase.getInstance()
            val myRef = database.getReference("marcadores")

    /// ... botão para adicionar marcador
            googleMap.addMarker().setOnClickListener {
                val marker = MarkerOptions()
                    .position(LatLng(latitude, longitude))
                    .title("Novo Marcador")
                googleMap.addMarker(marker)

                // Salvar no Firebase
                val marcador = Marcador(title = "Novo Marcador", latitude = latitude, longitude = longitude)
                myRef.push().setValue(marcador)
            }

            *//*
    }
*/

    //todo verifica se tem permissao para acessar a localização
    private fun verificaPermissaoUsuario() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //todo habilita a localização no mapa
    private fun habilitarBotaoMinhaLocalizacao() {

        if (!::googleMap.isInitialized) return

        if (verificaPermissaoUsuario()) {

            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            googleMap.isMyLocationEnabled = true
        } else {
            verificaPermissaoLocalizacao()
        }
    }

    //todo verifica se tem permissao para acessar a localização
    private fun verificaPermissaoLocalizacao() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION
            )
        }
    }

    //todo verifica se tem permissao para acessar a localização
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }

                googleMap.isMyLocationEnabled = true

            } else {

                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    //todo implementacao do metodo OnMyLocationButtonClickListener
    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(
            this, "Localização atual clicada com sucesso!", Toast.LENGTH_SHORT
        ).show()
        return false
    }

    /*
        override fun onMyLocationClick(localizacao: Location) {
            Toast.makeText(
                requireContext(),
                "voce esta em ${localizacao.latitude}, ${localizacao.longitude}",
                Toast.LENGTH_SHORT
            ).show()
        }
        */

    fun mundaCorPolylineRandom(polyline: Polyline) {
        val color = (0..3).random()
        when (color) {
            0 -> polyline.color = ContextCompat.getColor(this, R.color.red)
            1 -> polyline.color = ContextCompat.getColor(this, R.color.yellow)
            2 -> polyline.color = ContextCompat.getColor(this, R.color.green)
            3 -> polyline.color = ContextCompat.getColor(this, R.color.blue)
        }
    }

//////// GOOGLE MAP ////////

    //todo Atribuindo o Binding
    private val binding by lazy {

        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //todo firebase

//        FirebaseApp.initializeApp(this)


//        FirebaseFirestore.getInstance().collection("CleanMap").document("1").set("hola")

        FragmentGoogleMap()
    }

    fun FragmentGoogleMap() {
        val fragmentGoogleMap =
            supportFragmentManager.findFragmentById(R.id.fragmentGoogleMap) as SupportMapFragment
        fragmentGoogleMap.getMapAsync(this)
    }
}