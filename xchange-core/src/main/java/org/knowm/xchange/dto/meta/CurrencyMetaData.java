package org.knowm.xchange.dto.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class CurrencyMetaData implements Serializable {

  private static final long serialVersionUID = -247899067657358542L;

  @JsonProperty("scale")
  private final Integer scale;

  /** Withdrawal fee */
  @JsonProperty("withdrawal_fee")
  private final BigDecimal withdrawalFee;

  /** Minimum withdrawal amount */
  @JsonProperty("min_withdrawal_amount")
  private final BigDecimal minWithdrawalAmount;

  /** Wallet health */
  @JsonProperty("wallet_health")
  private WalletHealth walletHealth;

  @JsonProperty("is_borrowable")
  private boolean isBorrowable;

  @JsonProperty("withdraw_allowed")
  private boolean isWithdrawAllowed;

  @JsonProperty("deposit_allowed")
  private boolean isDepositAllowed;

  @JsonProperty("daily_interest")
  private BigDecimal interest;

  /**
   * Constructor
   *
   * @param scale
   * @param withdrawalFee
   */
  public CurrencyMetaData(Integer scale, BigDecimal withdrawalFee) {
    this(scale, withdrawalFee, null);
  }

  /**
   * Constructor
   *
   * @param scale
   * @param withdrawalFee
   * @param minWithdrawalAmount
   */
  public CurrencyMetaData(Integer scale, BigDecimal withdrawalFee, BigDecimal minWithdrawalAmount) {
    this(scale, withdrawalFee, minWithdrawalAmount, WalletHealth.UNKNOWN, BigDecimal.ZERO, false, false, false);
  }

  /**
   * Constructor
   *
   * @param scale
   * @param withdrawalFee
   * @param minWithdrawalAmount
   */
  public CurrencyMetaData(Integer scale, BigDecimal withdrawalFee, BigDecimal minWithdrawalAmount, WalletHealth health) {
    this(scale, withdrawalFee, minWithdrawalAmount, health, BigDecimal.ZERO, false, false, false);
  }

  /**
   * Constructor
   *
   * @param withdrawalFee
   * @param minWithdrawalAmount
   * @param isWithdrawAllowed
   * @param isDepositAllowed
   */
  public CurrencyMetaData(BigDecimal withdrawalFee, BigDecimal minWithdrawalAmount, boolean isWithdrawAllowed, boolean isDepositAllowed) {
    this(0, withdrawalFee, minWithdrawalAmount, WalletHealth.UNKNOWN, BigDecimal.ZERO, false, isWithdrawAllowed, isDepositAllowed);
  }

  /**
   * Constructor
   *
   * @param scale
   */
  public CurrencyMetaData(
      @JsonProperty("scale") Integer scale,
      @JsonProperty("withdrawal_fee") BigDecimal withdrawalFee,
      @JsonProperty("min_withdrawal_amount") BigDecimal minWithdrawalAmount,
      @JsonProperty("wallet_health") WalletHealth walletHealth,
      @JsonProperty("daily_interest") BigDecimal interest,
      @JsonProperty("is_borrowable") boolean isBorrowable,
      @JsonProperty("withdraw_allowed") boolean isWithdrawAllowed,
      @JsonProperty("deposit_allowed") boolean isDepositAllowed
      ) {
    this.scale = scale;
    this.withdrawalFee = withdrawalFee;
    this.minWithdrawalAmount = minWithdrawalAmount;
    this.walletHealth = walletHealth;
    this.interest = interest;
    this.isBorrowable = isBorrowable;
    this.isWithdrawAllowed = isWithdrawAllowed;
    this.isDepositAllowed = isDepositAllowed;
  }

  public Integer getScale() {
    return scale;
  }

  public BigDecimal getWithdrawalFee() {
    return withdrawalFee;
  }

  public BigDecimal getMinWithdrawalAmount() {
    return minWithdrawalAmount;
  }

  public BigDecimal getInterest() { return interest; }

  public void setInterest(BigDecimal interest) {
    if (interest == null) {
      this.interest = BigDecimal.ZERO;
    } else {
      this.interest = interest;
    }
  }

  public boolean isBorrowable() { return isBorrowable; }

  public void setIsBorrowable(boolean borrowable) { isBorrowable = borrowable; }

  public boolean isWithdrawAllowed() { return isWithdrawAllowed; }
  public boolean isDepositAllowed() { return isDepositAllowed; }

  public WalletHealth getWalletHealth() {
    return walletHealth;
  }

  @Override
  public String toString() {
    return "CurrencyMetaData ["
        + "scale="
        + scale
            + ", dailyInterest="
            + interest
        + ", withdrawalFee="
        + withdrawalFee
        + ", minWithdrawalAmount="
        + minWithdrawalAmount
        + ", walletHealth="
        + walletHealth
        + "]";
  }
}
