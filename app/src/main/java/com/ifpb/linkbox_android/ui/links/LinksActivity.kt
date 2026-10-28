package com.ifpb.linkbox_android.ui.links

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifpb.linkbox_android.LinkBoxApplication
import com.ifpb.linkbox_android.data.model.Link
import com.ifpb.linkbox_android.databinding.ActivityLinksBinding
import com.ifpb.linkbox_android.ui.viewmodel.AppViewModelFactory
import com.ifpb.linkbox_android.ui.viewmodel.LinkViewModel
import kotlinx.coroutines.launch

class LinksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLinksBinding
    private lateinit var adapter: LinkAdapter
    private var folderId: Long = -1L

    private val viewModel: LinkViewModel by viewModels {
        AppViewModelFactory(
            (application as LinkBoxApplication).folderRepository,
            (application as LinkBoxApplication).linkRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera os dados enviados pela MainActivity
        folderId = intent.getLongExtra("EXTRA_FOLDER_ID", -1L)
        val folderName = intent.getStringExtra("EXTRA_FOLDER_NAME") ?: "Links"

        // Atualiza o título da Toolbar/Actionbar, se existir
        supportActionBar?.title = folderName
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botão de voltar

        // Informa ao ViewModel qual pasta estamos acessando
        if (folderId != -1L) {
            viewModel.setFolderId(folderId)
        }

        setupRecyclerView()
        observeViewModel()

        binding.fabAddLink.setOnClickListener {
            // Inicia criação de link
            val intent = Intent(this, LinkFormActivity::class.java).apply {
                putExtra("EXTRA_FOLDER_ID", folderId)
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = LinkAdapter(
            onLinkClick = { link -> openUrlInBrowser(link.url) },
            onMenuClick = { view, link -> showPopupMenu(view, link) }
        )
        binding.rvLinks.layoutManager = LinearLayoutManager(this)
        binding.rvLinks.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.links.collect { links ->
                    adapter.submitList(links)
                    binding.tvEmptyState.visibility = if (links.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showPopupMenu(view: View, link: Link) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "Editar")
        popup.menu.add(0, 2, 1, "Excluir")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    // Inicia edição de link
                    val intent = Intent(this@LinksActivity, LinkFormActivity::class.java).apply {
                        putExtra("EXTRA_FOLDER_ID", link.folderId)
                        putExtra("EXTRA_LINK_ID", link.id)
                        putExtra("EXTRA_LINK_TITLE", link.title)
                        putExtra("EXTRA_LINK_URL", link.url)
                        putExtra("EXTRA_LINK_DESC", link.description)
                    }
                    startActivity(intent)
                    true
                }
                2 -> {
                    viewModel.delete(link)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // Esta função demonstra o conceito de Intent Implícita no Android.
    private fun openUrlInBrowser(url: String) {
        // Validação simples para evitar travamentos caso a URL não tenha http/https
        var parsedUrl = url
        // Validação preventiva: O navegador do Android exige o protocolo na URL,
        // então injetamos o HTTP caso o usuário tenha digitado apenas "www.site.com".
        if (!parsedUrl.startsWith("http://") && !parsedUrl.startsWith("https://")) {
            parsedUrl = "http://$parsedUrl"
        }

        try {
            // Em vez de chamar uma classe externa (BrowserActivity), usa ACTION_VIEW.
            // Aqui é pedido para abrir a URI com o melhor app disponivel no dispositivo.
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsedUrl))
            startActivity(intent)
        } catch (e: Exception) {
            // O bloco try/catch evita que o app feche abruptamente ao tentar abrir uma URL inválida.
            Toast.makeText(this, "Erro ao abrir o link. URL inválida.", Toast.LENGTH_SHORT).show()
        }
    }

    // Configura o botão de voltar da ActionBar nativa
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}