
public class First100PrimeNumbers {

	// Method for printing first 100 prime numbers
	static void print_first_N_prime(int N) {
		int i = 0, j = 0, flag;

		System.out.println("Prime numbers between 1 and " + N + " are:");

		// Traverse each number from 1 to N
		int x = 0;
		while (x <= N) {

			// Condition for 1 and 0 as they are neither prime nor composite
			if (i == 1 || i == 0) {
				i++;
				continue;
			}

			// Flag is 1 if number is prime
			flag = 1;

			for (j = 2; j <= i / 2; j++) {
				if (i % j == 0) {
					flag = 0;
					break;
				}
			}

			if (flag == 1) {
				System.out.print(i + " ");
				x++;
			}
			i++;

		}

	}

	public static void main(String[] args) {

		print_first_N_prime(100);
	}
}