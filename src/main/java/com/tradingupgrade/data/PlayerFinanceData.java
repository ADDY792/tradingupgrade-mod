package com.tradingupgrade.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Player-level financial data stored as a data attachment.
 * Tracks overall account info and credit card state.
 */
public record PlayerFinanceData(
        int accountBalance,
        int creditCardDebt,
        int creditCardLimit,
        long lastInterestDay,
        long totalDaysElapsed
) {

    public static final PlayerFinanceData DEFAULT = new PlayerFinanceData(0, 0, 100, 0, 0);

    public static final Codec<PlayerFinanceData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("accountBalance").forGetter(PlayerFinanceData::accountBalance),
                    Codec.INT.fieldOf("creditCardDebt").forGetter(PlayerFinanceData::creditCardDebt),
                    Codec.INT.fieldOf("creditCardLimit").forGetter(PlayerFinanceData::creditCardLimit),
                    Codec.LONG.fieldOf("lastInterestDay").forGetter(PlayerFinanceData::lastInterestDay),
                    Codec.LONG.fieldOf("totalDaysElapsed").forGetter(PlayerFinanceData::totalDaysElapsed)
            ).apply(instance, PlayerFinanceData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerFinanceData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, PlayerFinanceData::accountBalance,
                    ByteBufCodecs.INT, PlayerFinanceData::creditCardDebt,
                    ByteBufCodecs.INT, PlayerFinanceData::creditCardLimit,
                    ByteBufCodecs.VAR_LONG, PlayerFinanceData::lastInterestDay,
                    ByteBufCodecs.VAR_LONG, PlayerFinanceData::totalDaysElapsed,
                    PlayerFinanceData::new
            );

    // Builder-style methods for immutable updates
    public PlayerFinanceData withAccountBalance(int balance) {
        return new PlayerFinanceData(balance, creditCardDebt, creditCardLimit, lastInterestDay, totalDaysElapsed);
    }

    public PlayerFinanceData withCreditCardDebt(int debt) {
        return new PlayerFinanceData(accountBalance, debt, creditCardLimit, lastInterestDay, totalDaysElapsed);
    }

    public PlayerFinanceData withCreditCardLimit(int limit) {
        return new PlayerFinanceData(accountBalance, creditCardDebt, limit, lastInterestDay, totalDaysElapsed);
    }

    public PlayerFinanceData withLastInterestDay(long day) {
        return new PlayerFinanceData(accountBalance, creditCardDebt, creditCardLimit, day, totalDaysElapsed);
    }

    public PlayerFinanceData withTotalDaysElapsed(long days) {
        return new PlayerFinanceData(accountBalance, creditCardDebt, creditCardLimit, lastInterestDay, days);
    }

    /**
     * Deposit money into account.
     */
    public PlayerFinanceData deposit(int amount) {
        if (amount <= 0) return this;
        return withAccountBalance(accountBalance + amount);
    }

    /**
     * Withdraw money from account.
     * Returns null if insufficient funds.
     */
    public PlayerFinanceData withdraw(int amount) {
        if (amount <= 0 || amount > accountBalance) return null;
        return withAccountBalance(accountBalance - amount);
    }

    /**
     * Borrow money on credit card.
     * Returns null if exceeds credit limit.
     */
    public PlayerFinanceData borrow(int amount) {
        if (amount <= 0) return null;
        int available = creditCardLimit - creditCardDebt;
        if (amount > available) return null;
        return withCreditCardDebt(creditCardDebt + amount);
    }

    /**
     * Repay credit card debt.
     * If fully repaid, doubles the credit limit.
     * Returns the actual amount repaid.
     */
    public RepayResult repay(int amount) {
        if (amount <= 0) return new RepayResult(this, 0);
        int toRepay = Math.min(amount, Math.min(creditCardDebt, accountBalance));
        if (toRepay <= 0) return new RepayResult(this, 0);

        PlayerFinanceData updated = withAccountBalance(accountBalance - toRepay)
                .withCreditCardDebt(creditCardDebt - toRepay);

        // If fully repaid, double the credit limit
        if (updated.creditCardDebt() == 0 && creditCardDebt > 0) {
            updated = updated.withCreditCardLimit(creditCardLimit * 2);
        }

        return new RepayResult(updated, toRepay);
    }

    /**
     * Apply daily interest (5%) to credit card debt.
     */
    public PlayerFinanceData applyDailyInterest() {
        if (creditCardDebt <= 0) return this;
        int interest = (int) Math.ceil(creditCardDebt * 0.05);
        return withCreditCardDebt(creditCardDebt + interest);
    }

    public record RepayResult(PlayerFinanceData data, int amountRepaid) {}
}
