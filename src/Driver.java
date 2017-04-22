import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Aamir on 11/12/16.
 */
public class Driver {
    public static void main(String[] args) throws IOException{
        if(args.length==0 || args.length>1){
            System.out.println("Provide one argument i.e input file name.");
            System.exit(1);
        }
        File file = new File(args[0]);
        Scanner sc = new Scanner(System.in);
        while(true){
            Helper.displayMenu();
            int choice = sc.nextInt();

            switch (choice){
                case 1:
                    Simulator.init(file);
                    break;

                case 2:
                    System.out.println("\nHow many Cycles?");
                    int cycles = sc.nextInt();
                    Simulator.simulate(cycles);
                    break;

                case 3:
                    Simulator.display();
                    break;

                case 4:
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    break;
            }
        }
    }
}