package com.ankoki.skjade.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.ankoki.skjade.utils.Utils;
import jdk.jfr.Description;
import jdk.jfr.Name;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Roman Numerals")
@Description("Returns the given number in roman numerals.")
@Examples("send \"Sharpness level: %roman numeral of (level of sharpness of player's tool)%\"")
@Since("1.0.0")
public class ExprRomanNumerals extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprRomanNumerals.class, String.class, ExpressionType.PROPERTY,
                "[value of] %number% in roman numeral[s]");
    }

    private Expression<Integer> num;

    @Override
    protected String[] get(Event event) {
        if (num == null) return new String[]{""};
        int i = num.getSingle(event);
        return new String[]{Utils.toRoman(i)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "roman numeral";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        num = (Expression<Integer>) exprs[0];
        return true;
    }
}
