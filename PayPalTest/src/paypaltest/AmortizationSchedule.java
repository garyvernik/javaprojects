package paypaltest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AmortizationSchedule {

    // TODO GV: I am using BigDecimal pretty liberally. This works but comes
    // at performance price. Can I use long instead in some cases?
    
    private static final BigDecimal HUNDRED = new BigDecimal(100);
    private static final int CENTS = 100;
    private static final int MONTHS_YEAR = 12;
    private static final double MONTHLY_INTEREST_DIVISOR = 12d * CENTS;

    private final long amountBorrowed; // in cents
    private final double apr;
    private final int initialTermMonths;

    private double monthlyInterest;
    private final BigDecimal monthlyPaymentAmount; // in cents

    private long calculateMonthlyPayment() {
        // M = P * (J / (1 - (Math.pow(1/(1 + J), N))));
        //
        // Where:
        // P = Principal
        // I = Interest
        // J = Monthly Interest in decimal form: I / (12 * 100)
        // N = Number of months of loan
        // M = Monthly Payment Amount
        //

        // calculate J
        monthlyInterest = apr / MONTHLY_INTEREST_DIVISOR;

        // this is 1 / (1 + J)
        double tmp = Math.pow(1d + monthlyInterest, -1);

        // this is Math.pow(1/(1 + J), N)
        tmp = Math.pow(tmp, initialTermMonths);

        // this is 1 / (1 - (Math.pow(1/(1 + J), N))))
        tmp = Math.pow(1d - tmp, -1);

        // M = P * (J / (1 - (Math.pow(1/(1 + J), N))));
        double rc = amountBorrowed * monthlyInterest * tmp;
        
        // GV: round the ceiling of the number. It becomes important when 
        // dealing with the large numbers where the fraction of a cent makes the
        // difference between reducing and increasing the debt
        return Math.round(Math.ceil(rc));
    }

    // The output should include:
    // The first column identifies the payment number.
    // The second column contains the amount of the payment.
    // The third column shows the amount paid to interest.
    // The fourth column has the current balance. The total payment amount and
    // the interest paid fields.
    //
    // GV: separated the business logic from presentation. Now this method 
    // returns the list of monthly amortization schedule items which can be
    // handled (printed/processed/etc.) later on. 
    // GV: all IO operations are moved into a separate utility class
    public List<AmortizationScheduleItem> outputAmortizationSchedule() {
        //
        // To create the amortization table, create a loop in your program and
        // follow these steps:
        // 1. Calculate H = P x J, this is your current monthly interest
        // 2. Calculate C = M - H, this is your monthly payment minus your
        // monthly interest, so it is the amount of principal you pay for that
        // month
        // 3. Calculate Q = P - C, this is the new balance of your principal of
        // your loan.
        // 4. Set P equal to Q and go back to Step 1: You thusly loop around
        // until the value Q (and hence P) goes to zero.
        //

        // GV: borrowing ~1/3 of US Federal Budget ($1T) for million years
        // under gangester's 100% rate can easily overflow long for payments and 
        // interest paid. Using BigDecimal.
        BigDecimal balance = new BigDecimal(amountBorrowed);
        int paymentNumber = 0;
        BigDecimal totalPayments = BigDecimal.ZERO;
        BigDecimal totalInterestPaid = BigDecimal.ZERO;

        List<AmortizationScheduleItem> items = new ArrayList<AmortizationScheduleItem>();

        items.add(new AmortizationScheduleItem(paymentNumber++,
                BigDecimal.ZERO, BigDecimal.ZERO, 
                (new BigDecimal(amountBorrowed)).divide(HUNDRED), 
                totalPayments.divide(HUNDRED), totalInterestPaid.divide(HUNDRED)));

        final int maxNumberOfPayments = initialTermMonths + 1;
        BigDecimal bdMonthlyInterest = new BigDecimal(monthlyInterest);
        while (balance.compareTo(BigDecimal.ZERO) > 0
                && paymentNumber <= maxNumberOfPayments) {
            // Calculate H = P x J, this is your current monthly interest
            BigDecimal curMonthlyInterest = balance.multiply(bdMonthlyInterest);

            // the amount required to payoff the loan
            BigDecimal curPayoffAmount = balance.add(curMonthlyInterest);

            // the amount to payoff the remaining balance may be less than the
            // calculated monthlyPaymentAmount
            BigDecimal curMonthlyPaymentAmount = 
                    monthlyPaymentAmount.min(curPayoffAmount);

            // it's possible that the calculated monthlyPaymentAmount is 0,
            // or the monthly payment only covers the interest payment - i.e. no
            // principal
            // so the last payment needs to payoff the loan
            if (paymentNumber == maxNumberOfPayments
                    && (curMonthlyPaymentAmount.equals(BigDecimal.ZERO) 
                        || curMonthlyPaymentAmount.equals(curMonthlyInterest))) {
                curMonthlyPaymentAmount = curPayoffAmount;
            }

            // Calculate C = M - H, this is your monthly payment minus your
            // monthly interest,
            // so it is the amount of principal you pay for that month
            BigDecimal curMonthlyPrincipalPaid = 
            		curMonthlyPaymentAmount.subtract(curMonthlyInterest);

            // Calculate Q = P - C, this is the new balance of your principal of
            // your loan.
            BigDecimal curBalance = balance.subtract(curMonthlyPrincipalPaid);

            totalPayments = totalPayments.add(curMonthlyPaymentAmount);
            totalInterestPaid = totalInterestPaid.add(curMonthlyInterest);

            items.add(new AmortizationScheduleItem(paymentNumber++,
                    curMonthlyPaymentAmount.divide(HUNDRED), 
                    curMonthlyInterest.divide(HUNDRED), 
                    curBalance.divide(HUNDRED),
                    totalPayments.divide(HUNDRED), 
                    totalInterestPaid.divide(HUNDRED)));

            // Set P equal to Q and go back to Step 1: You thusly loop around
            // until the value Q (and hence P) goes to zero.
            balance = curBalance;
        }

        return items;
    }

    /**
     * Constructor for AmortizationSchedule
     * 
     * @param amount amount to borrow
     * @param interestRate rate
     * @param years terms
     * @throws IllegalArgumentException
     */
    public AmortizationSchedule(final double amount, final double interestRate, final int years) {

        if (!Validators.getBorrowAmountValidator().isValid(amount)
                || !Validators.getAprValidator().isValid(interestRate)
                || !Validators.getTermValidator().isValid((double) years)) {
            throw new IllegalArgumentException();
        }

        amountBorrowed = Math.round(amount * CENTS);
        apr = interestRate;
        initialTermMonths = years * MONTHS_YEAR;

        monthlyPaymentAmount = new BigDecimal(calculateMonthlyPayment());

        // the following shouldn't happen with the available valid ranges
        // for borrow amount, apr, and term; however, without range validation,
        // monthlyPaymentAmount as calculated by calculateMonthlyPayment()
        // may yield incorrect values with extreme input values
        if (monthlyPaymentAmount.compareTo(new BigDecimal(amountBorrowed)) > 0) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {

        final String[] userPrompts = {
                "Please enter the amount you would like to borrow: ",
                "Please enter the annual percentage rate used to repay the loan: ",
                "Please enter the term, in years, over which the loan is repaid: " };

        double amount = 0;
        double apr = 0;
        int years = 0;

        try {
            amount = getValue(userPrompts[0],
                    Validators.getBorrowAmountValidator(),
                    "Please enter a positive value between "
                            + Validators.BORROW_AMOUNT_RANGE.getMin() + " and "
                            + Validators.BORROW_AMOUNT_RANGE.getMax() + ". ");
            apr = getValue(userPrompts[1], Validators.getAprValidator(),
                    "Please enter a positive value between "
                            + Validators.APR_RANGE.getMin() + " and "
                            + Validators.APR_RANGE.getMax() + ". ");
            years = getValue(
                    userPrompts[2],
                    Validators.getBorrowAmountValidator(),
                    "Please enter a positive integer between "
                            + Validators.TERM_RANGE.getMin() + " and "
                            + Validators.TERM_RANGE.getMax() + ". ").intValue();
        } catch (IOException e) {
            IOUtils.print("An IOException was encountered. Terminating program.\n");
            return;
        }

        try {
            AmortizationSchedule as = new AmortizationSchedule(amount, apr, years);
            // AmortizationSchedule as = new
            // AmortizationSchedule(1000000000000d, 100d, 33);
            // AmortizationSchedule as = new AmortizationSchedule(200000d, 6.3d, 20);
            IOUtils.printSchedule(as.outputAmortizationSchedule());
        } catch (IllegalArgumentException e) {
            IOUtils.print("Unable to process the values entered. Terminating program.\n");
        }
    }

    private static Double getValue(final String promptMessage,
            final Validatable<Double> validator, final String invalidMessage)
            throws IOException {
        boolean isValid = false;
        double amount = 0;
        while (!isValid) {
            String line = IOUtils.readLine(promptMessage);

            try {
                amount = Double.valueOf(line);
                if (validator.isValid(amount)) {
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                IOUtils.print("An invalid value was entered.\n");
            }

            if (!isValid) {
                IOUtils.print(invalidMessage);
            }
        }

        return amount;
    }
}
