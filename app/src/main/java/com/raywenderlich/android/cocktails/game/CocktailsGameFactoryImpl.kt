package com.raywenderlich.android.cocktails.game

import com.raywenderlich.android.cocktails.common.network.Cocktail
import com.raywenderlich.android.cocktails.common.repository.CocktailsRepository
import com.raywenderlich.android.cocktails.common.repository.RepositoryCallback
import com.raywenderlich.android.cocktails.game.model.Game

class CocktailsGameFactoryImpl(private val repository: CocktailsRepository) : CocktailsGameFactory {
    override fun buildGame(callback: CocktailsGameFactory.Callback) {
        repository.getAlcoholic(
                object : RepositoryCallback<List<Cocktail>, String> {
                    override fun onSuccess(t: List<Cocktail>) {
                        callback.onSuccess(Game(emptyList()))
                    }

                    override fun onError(e: String) {
                        callback.onError()
                    }
                }
        )
    }


}