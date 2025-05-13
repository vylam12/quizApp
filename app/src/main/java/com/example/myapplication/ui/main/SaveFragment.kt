package com.example.myapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.R
import com.example.myapplication.repositori.TranslateResponse
import com.example.myapplication.ui.translate.VocabularyActivity
import com.example.myapplication.utils.UserPreferences
import com.example.myapplication.viewmodel.TranslateViewModel

class SaveFragment : Fragment() {

    private val translateViewModel: TranslateViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedVocabAdapter
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var edtSearch: EditText

    private var allVocabList: List<TranslateResponse.Vocab> = emptyList()
    private var currentQuery: String = ""
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_save, container, false)
        recyclerView = view.findViewById(R.id.rcSaveList)
        lottieAnimationView = view.findViewById(R.id.loading_save)
        edtSearch = view.findViewById(R.id.edtSearch)

        userId = UserPreferences.getUserId(requireContext()) ?: ""

        setupRecyclerView()
        setupSearch()
        fetchCachedData()

        return view
    }

    private fun setupRecyclerView() {
        adapter = SavedVocabAdapter(
            emptyList(),
            onUnsaveClick = { vocab ->
                translateViewModel.deleteVocabulary(vocab._idUserVocabulary)
                Toast.makeText(requireContext(), "Removed from saved", Toast.LENGTH_SHORT).show()

                val updatedVocab = vocab.copy(isSaved = false, _idUserVocabulary = "")
                allVocabList = allVocabList.map {
                    if (it.id == vocab.id) updatedVocab else it
                }

                adapter.updateList(filterList(currentQuery))
                translateViewModel.savedVocabList = allVocabList
            },
            onItemClick = { vocab ->
                val intent = Intent(requireContext(), VocabularyActivity::class.java)
                intent.putExtra("WORD", vocab.word)
                startActivity(intent)
            },
            onSaveClick = { vocab ->
                translateViewModel.saveUserVocab(userId, vocab.id)
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()

                val updatedVocab = vocab.copy(isSaved = true, _idUserVocabulary = "")
                allVocabList = allVocabList.map {
                    if (it.id == vocab.id) updatedVocab else it
                }

                adapter.updateList(filterList(currentQuery))
                translateViewModel.savedVocabList = allVocabList
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s.toString().trim().lowercase()
                adapter.updateList(filterList(currentQuery))

                if (currentQuery.isNotEmpty() && adapter.itemCount == 0) {
                    searchNewWord(currentQuery)
                }
            }
        })
    }

    private fun loadData() {
        showLoading(true)
        translateViewModel.getListVocab(userId)
        translateViewModel.getListVocabResponse.observe(viewLifecycleOwner) { res ->
            res?.let {
                allVocabList = it.data
                translateViewModel.savedVocabList = it.data

                adapter.updateList(allVocabList)
                showLoading(false)
            }
        }
    }

    private fun fetchCachedData() {
        val cachedData = translateViewModel.savedVocabList
        if (cachedData != null) {
            allVocabList = cachedData
            adapter.updateList(filterList(currentQuery))
            showLoading(false)
        } else {
            loadData()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        lottieAnimationView.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        edtSearch.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun filterList(query: String): List<TranslateResponse.Vocab> {
        if (query.isEmpty()) return allVocabList

        return allVocabList.filter { vocab ->
            vocab.word.lowercase().startsWith(query)
        }
    }

    private fun searchNewWord(query: String) {
        translateViewModel.findVocabulary(userId, query)
        translateViewModel.findVocabularyResponse.observe(viewLifecycleOwner) { res ->
            res?.let {
                if (it.newWord.word.lowercase() == currentQuery.lowercase()) {
                    val newVocab = TranslateResponse.Vocab(
                        id = it.newWord._id,
                        word = it.newWord.word,
                        phonetic = it.newWord.phonetics.getOrNull(0)?.text ?: "",
                        meaning = it.newWord.meanings.getOrNull(0)?.definitions?.getOrNull(0)?.definition ?: "",
                        audio = it.newWord.phonetics.getOrNull(0)?.audio ?: "",
                        isSaved = false,
                        _idUserVocabulary = ""
                    )
                    adapter.updateList(listOf(newVocab))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCachedData()
    }
}
