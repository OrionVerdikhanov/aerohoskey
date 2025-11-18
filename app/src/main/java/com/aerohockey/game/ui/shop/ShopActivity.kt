package com.aerohockey.game.ui.shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.aerohockey.game.R
import com.aerohockey.game.data.repository.PlayerProgressRepository
import com.aerohockey.game.databinding.ActivityShopBinding
import com.aerohockey.game.domain.progression.Skin
import com.aerohockey.game.domain.progression.SkinType
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Магазин скинов для шайб и бит
 */
class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var progressRepository: PlayerProgressRepository
    private lateinit var skinAdapter: SkinAdapter

    private var currentSkins = listOf<Skin>()
    private var currentCoins = 0
    private var currentLevel = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressRepository = PlayerProgressRepository(this)

        setupToolbar()
        setupRecyclerView()
        setupTabs()
        loadShopData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        skinAdapter = SkinAdapter(
            onSkinClick = { skin -> handleSkinClick(skin) }
        )

        binding.rvSkins.apply {
            layoutManager = GridLayoutManager(this@ShopActivity, 2)
            adapter = skinAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> filterSkins(SkinType.PUCK)
                    1 -> filterSkins(SkinType.PADDLE)
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadShopData() {
        lifecycleScope.launch {
            // Загрузка монет
            progressRepository.getCoins().collect { coins ->
                currentCoins = coins
                updateCoinsDisplay()
            }
        }

        lifecycleScope.launch {
            // Загрузка уровня
            progressRepository.getPlayerLevel().collect { playerLevel ->
                currentLevel = playerLevel.currentLevel
            }
        }

        lifecycleScope.launch {
            // Загрузка скинов
            progressRepository.getUnlockedSkins().collect { unlockedIds ->
                currentSkins = Skin.ALL_SKINS.map { skin ->
                    skin.copy(
                        isUnlocked = unlockedIds.contains(skin.id),
                        isEquipped = false // Будет обновлено ниже
                    )
                }
                filterSkins(SkinType.PUCK)
            }
        }

        lifecycleScope.launch {
            // Загрузка экипированных скинов
            val equippedPuck = progressRepository.getEquippedSkin(SkinType.PUCK).first()
            val equippedPaddle = progressRepository.getEquippedSkin(SkinType.PADDLE).first()

            currentSkins = currentSkins.map { skin ->
                when (skin.type) {
                    SkinType.PUCK -> skin.copy(isEquipped = skin.id == equippedPuck)
                    SkinType.PADDLE -> skin.copy(isEquipped = skin.id == equippedPaddle)
                }
            }
            filterSkins(SkinType.PUCK)
        }
    }

    private fun filterSkins(type: SkinType) {
        val filtered = currentSkins.filter { it.type == type }
        skinAdapter.submitList(filtered)
    }

    private fun updateCoinsDisplay() {
        binding.tvCoins.text = currentCoins.toString()
    }

    private fun handleSkinClick(skin: Skin) {
        when {
            skin.isEquipped -> {
                // Уже экипирован
                Snackbar.make(binding.root, "Уже экипирован", Snackbar.LENGTH_SHORT).show()
            }
            skin.isUnlocked -> {
                // Экипировать
                lifecycleScope.launch {
                    progressRepository.equipSkin(skin.id, skin.type)
                    Snackbar.make(binding.root, "✓ ${skin.name} экипирован", Snackbar.LENGTH_SHORT).show()
                }
            }
            currentLevel < skin.unlockLevel -> {
                // Недостаточный уровень
                Snackbar.make(
                    binding.root,
                    "Требуется уровень ${skin.unlockLevel}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            currentCoins < skin.price -> {
                // Недостаточно монет
                Snackbar.make(
                    binding.root,
                    "Недостаточно монет (нужно ${skin.price})",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            else -> {
                // Купить
                lifecycleScope.launch {
                    val success = progressRepository.spendCoins(skin.price)
                    if (success) {
                        progressRepository.unlockSkin(skin.id)
                        Snackbar.make(
                            binding.root,
                            "🎉 ${skin.name} куплен!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
