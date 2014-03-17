package paypaltest;

import java.math.BigDecimal;

public class AmortizationScheduleItem {

	private int paymentNumber;
	private BigDecimal paymentAmount;
	private BigDecimal paymentInterest;
	private BigDecimal currentBalance;
	private BigDecimal totalPayments;
	private BigDecimal totalInterestPaid;

	public AmortizationScheduleItem(final int paymentNumber, final BigDecimal paymentAmount,
			final BigDecimal paymentInterest, final BigDecimal currentBalance,
			final BigDecimal totalPayments, final BigDecimal totalInterestPaid) {
		super();
		this.paymentNumber = paymentNumber;
		this.paymentAmount = paymentAmount;
		this.paymentInterest = paymentInterest;
		this.currentBalance = currentBalance;
		this.totalPayments = totalPayments;
		this.totalInterestPaid = totalInterestPaid;
	}

	public int getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(int paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getPaymentInterest() {
		return paymentInterest;
	}

	public void setPaymentInterest(BigDecimal paymentInterest) {
		this.paymentInterest = paymentInterest;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}

	public BigDecimal getTotalPayments() {
		return totalPayments;
	}

	public void setTotalPayments(BigDecimal totalPayments) {
		this.totalPayments = totalPayments;
	}

	public BigDecimal getTotalInterestPaid() {
		return totalInterestPaid;
	}

	public void setTotalInterestPaid(BigDecimal totalInterestPaid) {
		this.totalInterestPaid = totalInterestPaid;
	}

}
