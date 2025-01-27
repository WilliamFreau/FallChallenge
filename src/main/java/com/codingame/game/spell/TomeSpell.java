package com.codingame.game.spell;

import com.codingame.game.Recipe;

import java.util.stream.IntStream;

public class TomeSpell extends Spell {
    public int stock;
    private boolean repeatable;

    public TomeSpell(Recipe recipe) {
        stock = 0;
        this.recipe = recipe;
        repeatable = IntStream.of(recipe.delta)
            .anyMatch(x -> x < 0);
    }

    @Override
    public int getStock() {
        return stock;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }
}
