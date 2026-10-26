package com.kamu.kuissekolah

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class QuizActivity : AppCompatActivity() {

    /**
     * Model data satu soal pilihan ganda.
     * level       : kategori jenjang ("SD", "SMP", "SMA", "SMK")
     * question    : teks pertanyaan
     * options     : 4 opsi jawaban (index 0..3)
     * correctIndex: indeks jawaban benar (0..3)
     * explanation : teks penjelasan "Smart Correction"
     */
    data class Question(
        val level: String,
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String
    )

    // ---- Views ----
    private lateinit var tvCategory: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var tvExplanation: TextView
    private lateinit var cardExplanation: View
    private lateinit var btnPaham: MaterialButton
    private lateinit var optionButtons: List<MaterialButton>

    // ---- State ----
    private val questions = mutableListOf<Question>()
    private var currentIndex = 0
    private var score = 0
    private var answered = false

    // ---- Colors ----
    private val COLOR_DEFAULT = "#3F51B5"
    private val COLOR_CORRECT = "#4CAF50"
    private val COLOR_WRONG   = "#F44336"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        bindViews()
        setupListeners()
        buildQuestionBank()
        questions.shuffle()          // urutan acak setiap sesi
        loadQuestion()
    }

    // ------------------------------------------------------------------
    //  INISIALISASI
    // ------------------------------------------------------------------

    private fun bindViews() {
        tvCategory        = findViewById(R.id.tvCategory)
        tvQuestionNumber  = findViewById(R.id.tvQuestionNumber)
        tvScore           = findViewById(R.id.tvScore)
        tvQuestion        = findViewById(R.id.tvQuestion)
        tvExplanation     = findViewById(R.id.tvExplanation)
        cardExplanation   = findViewById(R.id.cardExplanation)
        btnPaham          = findViewById(R.id.btnPaham)

        optionButtons = listOf(
            findViewById(R.id.btnOptionA),
            findViewById(R.id.btnOptionB),
            findViewById(R.id.btnOptionC),
            findViewById(R.id.btnOptionD)
        )
    }

    private fun setupListeners() {
        optionButtons.forEachIndexed { index, button ->
            button.setOnClickListener { onOptionSelected(index) }
        }
        btnPaham.setOnClickListener {
            cardExplanation.visibility = View.GONE
            goToNextQuestion()
        }
    }

    // ------------------------------------------------------------------
    //  BANK SOAL (SD, SMP, SMA, SMK)
    // ------------------------------------------------------------------

    private fun buildQuestionBank() {
        /* ================= SD ================= */
        questions.add(
            Question(
                level = "SD",
                question = "Hasil dari 12 × 8 adalah ...",
                options = listOf("86", "96", "108", "92"),
                correctIndex = 1,
                explanation = "12 × 8 = 96. Bisa dihitung dengan memecah: (10 × 8) + (2 × 8) = 80 + 16 = 96."
            )
        )
        questions.add(
            Question(
                level = "SD",
                question = "Siapa presiden pertama Republik Indonesia?",
                options = listOf("Soeharto", "Ir. Soekarno", "B.J. Habibie", "Abdurrahman Wahid"),
                correctIndex = 1,
                explanation = "Ir. Soekarno adalah presiden pertama RI yang menjabat sejak 1945 hingga 1967."
            )
        )
        questions.add(
            Question(
                level = "SD",
                question = "Hewan berikut yang bernapas menggunakan insang adalah ...",
                options = listOf("Kucing", "Burung", "Ikan", "Sapi"),
                correctIndex = 2,
                explanation = "Ikan bernapas menggunakan insang untuk mengambil oksigen yang terlarut di dalam air."
            )
        )

        /* ================= SMP ================= */
        questions.add(
            Question(
                level = "SMP",
                question = "Jika 3x + 5 = 20, maka nilai x adalah ...",
                options = listOf("3", "5", "7", "15"),
                correctIndex = 1,
                explanation = "3x + 5 = 20 → 3x = 20 − 5 = 15 → x = 15 ÷ 3 = 5."
            )
        )
        questions.add(
            Question(
                level = "SMP",
                question = "Ibu kota Provinsi Jawa Barat adalah ...",
                options = listOf("Semarang", "Surabaya", "Bandung", "Medan"),
                correctIndex = 2,
                explanation = "Bandung adalah ibu kota Provinsi Jawa Barat, sering dijuluki 'Kota Kembang'."
            )
        )
        questions.add(
            Question(
                level = "SMP",
                question = "Bagian tumbuhan yang berperan utama dalam fotosintesis adalah ...",
                options = listOf("Akar", "Daun", "Batang", "Bunga"),
                correctIndex = 1,
                explanation = "Daun mengandung klorofil yang menangkap cahaya matahari untuk proses fotosintesis."
            )
        )

        /* ================= SMA ================= */
        questions.add(
            Question(
                level = "SMA",
                question = "Turunan pertama dari f(x) = 3x² + 2x adalah ...",
                options = listOf("6x + 2", "3x + 2", "6x²", "6x"),
                correctIndex = 0,
                explanation = "Gunakan aturan turunan: d/dx (3x²) = 6x dan d/dx (2x) = 2, sehingga f'(x) = 6x + 2."
            )
        )
        questions.add(
            Question(
                level = "SMA",
                question = "Hukum II Newton dirumuskan sebagai ...",
                options = listOf("F = m / a", "F = m + a", "F = m × a", "F = a / m"),
                correctIndex = 2,
                explanation = "Hukum II Newton menyatakan bahwa gaya = massa × percepatan (F = m·a)."
            )
        )
        questions.add(
            Question(
                level = "SMA",
                question = "Organel sel yang berperan sebagai tempat respirasi sel adalah ...",
                options = listOf("Ribosom", "Mitokondria", "Nukleus", "Lisosom"),
                correctIndex = 1,
                explanation = "Mitokondria dijuluki 'the powerhouse of the cell' karena menghasilkan ATP melalui respirasi."
            )
        )

        /* ================= SMK ================= */
        questions.add(
            Question(
                level = "SMK",
                question = "HTML merupakan singkatan dari ...",
                options = listOf(
                    "HyperText Markup Language",
                    "HighText Machine Language",
                    "HyperTool Multi Language",
                    "Home Tool Markup Language"
                ),
                correctIndex = 0,
                explanation = "HTML = HyperText Markup Language, bahasa markup standar untuk membangun halaman web."
            )
        )
        questions.add(
            Question(
                level = "SMK",
                question = "Persamaan dasar akuntansi yang benar adalah ...",
                options = listOf(
                    "Aset = Ekuitas − Liabilitas",
                    "Aset = Liabilitas + Ekuitas",
                    "Ekuitas = Aset + Liabilitas",
                    "Liabilitas = Aset + Ekuitas"
                ),
                correctIndex = 1,
                explanation = "Persamaan dasar akuntansi: Aset = Liabilitas + Ekuitas. Total harta sama dengan total kewajiban ditambah modal."
            )
        )
        questions.add(
            Question(
                level = "SMK",
                question = "Satuan internasional untuk daya listrik adalah ...",
                options = listOf("Volt", "Ampere", "Ohm", "Watt"),
                correctIndex = 3,
                explanation = "Daya listrik diukur dalam Watt (W). Volt = tegangan, Ampere = arus, Ohm = hambatan."
            )
        )
    }

    // ------------------------------------------------------------------
    //  SIKLUS TAMPIL SOAL
    // ------------------------------------------------------------------

    private fun loadQuestion() {
        answered = false
        resetButtons()
        cardExplanation.visibility = View.GONE

        if (currentIndex >= questions.size) {
            showResult()
            return
        }

        val q = questions[currentIndex]
        tvCategory.text       = "Jenjang: ${q.level}"
        tvQuestionNumber.text = "Soal ${currentIndex + 1}/${questions.size}"
        tvScore.text          = "Skor: $score"
        tvQuestion.text       = q.question

        val labels = listOf("A", "B", "C", "D")
        optionButtons.forEachIndexed { i, btn ->
            btn.text       = "${labels[i]}. ${q.options[i]}"
            btn.isEnabled  = true
        }
    }

    // ------------------------------------------------------------------
    //  LOGIKA JAWABAN
    // ------------------------------------------------------------------

    private fun onOptionSelected(selectedIndex: Int) {
        if (answered) return
        answered = true

        val q = questions[currentIndex]
        optionButtons.forEach { it.isEnabled = false }

        if (selectedIndex == q.correctIndex) {
            // ---- Jawaban BENAR ----
            score += 10
            tvScore.text = "Skor: $score"
            setButtonTint(optionButtons[selectedIndex], COLOR_CORRECT)

            // Lanjut otomatis setelah jeda singkat
            Handler(Looper.getMainLooper()).postDelayed({ goToNextQuestion() }, 900)
        } else {
            // ---- Jawaban SALAH → tampilkan Smart Correction ----
            setButtonTint(optionButtons[selectedIndex], COLOR_WRONG)
            setButtonTint(optionButtons[q.correctIndex], COLOR_CORRECT)

            tvExplanation.text = q.explanation
            cardExplanation.visibility = View.VISIBLE
        }
    }

    private fun goToNextQuestion() {
        currentIndex++
        loadQuestion()
    }

    // ------------------------------------------------------------------
    //  HASIL AKHIR
    // ------------------------------------------------------------------

    private fun showResult() {
        val total    = questions.size
        val maxScore = total * 10

        AlertDialog.Builder(this)
            .setTitle("Kuis Selesai!")
            .setMessage("Skor kamu: $score / $maxScore\n\nTerus berlatih agar makin hebat ya!")
            .setCancelable(false)
            .setPositiveButton("Ulangi") { _, _ ->
                currentIndex = 0
                score = 0
                questions.shuffle()
                loadQuestion()
            }
            .setNegativeButton("Keluar") { _, _ -> finish() }
            .show()
    }

    // ------------------------------------------------------------------
    //  UTIL WARNA TOMBOL
    // ------------------------------------------------------------------

    private fun resetButtons() {
        optionButtons.forEach { btn ->
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(COLOR_DEFAULT))
            btn.setTextColor(Color.WHITE)
        }
    }

    private fun setButtonTint(button: MaterialButton, hexColor: String) {
        button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hexColor))
        button.setTextColor(Color.WHITE)
    }
}
