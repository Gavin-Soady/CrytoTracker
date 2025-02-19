package ie.wit.cryptotracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.cryptotracker.R
import ie.wit.cryptotracker.adapters.CryptoAdapter
import ie.wit.cryptotracker.adapters.CryptoListener
import ie.wit.cryptotracker.databinding.CryptoListBinding
import ie.wit.cryptotracker.main.MainApp
import ie.wit.cryptotracker.models.CryptoModel
import ie.wit.cryptotracker.activities.CryptoActivity
import timber.log.Timber
import java.text.NumberFormat


class CryptoListActivity : AppCompatActivity(), CryptoListener {

    lateinit var app: MainApp
    private lateinit var binding: CryptoListBinding
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = CryptoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)
        app = application as MainApp
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        loadCryptos()
        registerRefreshCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, CryptoActivity::class.java)
                refreshIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCryptoClick(crypto: CryptoModel) {
        val launcherIntent = Intent(this, CryptoActivity::class.java)
        launcherIntent.putExtra("crypto_edit", crypto)
        refreshIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { loadCryptos()

            }
    }

    private fun loadCryptos() {
        showCryptos(app.cryptos.findAll())
    }

    fun showCryptos (cryptos: List<CryptoModel>) {
        binding.recyclerView.adapter = CryptoAdapter(cryptos, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
        var totalsNF = NumberFormat.getInstance().format(getTotalBalance())
        var totalBalance = "€$totalsNF"
        binding.totalAmount.setText(totalBalance)
    }

    private fun getTotalBalance(): Float {
        var allCryptos = app.cryptos.findAll()
        var totals = 0.0f
        //allCryptos.forEach {crypto -totalBalance+total}
        for (crypto in allCryptos){
            totals += (crypto.total).toFloat()
        }

        return totals
    }

}