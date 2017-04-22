import java.text.NumberFormat;
import java.util.*;

/**
 * Created by Aamir on 11/13/16.
 */
public class Helper {

    /**
     * Prints Simulator Menu on screen
     */
    public static void displayMenu(){
        System.out.print("\nAPEX Simulator");
        System.out.println("\nEnter   1   to Initialize");
        System.out.println("\nEnter   2   to Simulate Simulator");
        System.out.println("\nEnter   3   to Display Pipeline, Register and Memory Contents");
        System.out.println("\nEnter   4   to EXIT");
        System.out.println("\nEnter Option:");
    }

    public static String printPipeLine(String i, String s1, String s2, String s3, String s4, String s5, String s6){
        return makeOrder(i)+makeOrder(s1)+makeOrder(s2)+makeOrder(s3)+makeOrder(s4)+makeOrder(s5)+makeOrder(s6);
    }

    /**
     * Method to justify a string
     * @param s1 string to be jsutified
     * @return justified string
     */
    public  static String makeOrder(String s1){
        if(s1==null){
            return (repeat(" ",8)+null+repeat(" ",8));
        }
        if(s1.length()%2==0){
            int x = s1.length();
            int y = (20-x)/2;
            String res = repeat(" ",y)+s1+repeat(" ",y);
            return res;
        } else if(s1.length()%2!=0){
            int x = s1.length()-1;
            int y = (20-x)/2;
            String res = repeat(" ",y)+s1+repeat(" ",y);
            return res;
        }
        return "s1 not string or null";
    }


    /**
     * Method to repeat characters/strings
     * n number of times
     * @param str string to be repeated
     * @param num n number of times
     * @return repeated string
     */
    public static String repeat(String str, int num){
        String rep = str;
        for(int i=0;i<num;i++){
            rep+=str;
        }
        return rep;
    }


    /**
     * Method to determine if instruction is
     * one op, two op or three op.
     * @param instruction
     * @return category based on number of ops
     */
    public static Integer getInstructionCategory(String instruction){
        String[] parts = instruction.split("[,\\s]+");
        String temp = parts[0];
        if(temp.equalsIgnoreCase("BZ") || temp.equalsIgnoreCase("BNZ")){
            return 1;
        } else if(temp.equalsIgnoreCase("MOVC") || temp.equalsIgnoreCase("JUMP") ||
                temp.equalsIgnoreCase("BAL")){
            return 2;
        } else if(temp.equalsIgnoreCase("ADD") || temp.equalsIgnoreCase("SUB") ||
                temp.equalsIgnoreCase("MUL") || temp.equalsIgnoreCase("OR") ||
                temp.equalsIgnoreCase("EX-OR") || temp.equalsIgnoreCase("AND") ||
                temp.equalsIgnoreCase("LOAD") || temp.equalsIgnoreCase("STORE")){
            return 3;
        }
        return 0;
    }

    /**
     * Method to decode a two op instruction
     * @param Register
     * @param instruction
     * @return decoded instruction
     */
    public static String decodeTwoOpInstruction(HashMap<String,Integer> Register,String instruction){
        String[] parts = instruction.split("[,\\s]+");
        String op1 = parts[1];
        String op2 = parts[2];
        String numberOnly= op2.replaceAll("[^-?0-9]", "");
        switch (parts[0]){
            case "MOVC":
                instruction = parts[0]+" "+parts[1]+" "+numberOnly;
                break;
            case "JUMP":
            case "BAL":
                instruction = parts[0]+" "+getValueFromRegister(Register,parts[1])+" "+numberOnly;
                break;
            default:
                break;
        }
        return instruction;
    }

    /**
     * Method to execute a three op instruction
     * @param Register
     * @param instruction
     * @return ececuted instruction with values
     */
    public static String executeThreeOpInstructionLatch(HashMap<String,Integer> Register,String instruction){
        String[] parts = instruction.split("[,\\s]+");
//        System.out.println(parts[0]+" PARTS 0");
        String op1 = parts[1];
//        System.out.println("op1 "+op1);
        Integer op2 = Integer.parseInt(parts[2]);
//        System.out.println("op2 "+op2);
        Integer op3 = Integer.parseInt(parts[3]);
        switch (parts[0]){
            case "ADD":
                Integer add = op2+op3;
                instruction = parts[0]+" "+op1+" "+ add.toString();
                break;
            case "MUL":
                Integer mul = op2*op3;
                instruction = parts[0]+" "+op1+" "+mul.toString();
                break;
            case "SUB":
                Integer sub = op2-op3;
                instruction = parts[0]+" "+op1+" "+sub.toString();
                break;
            case "OR":
                Integer or = op2|op3;
                instruction= parts[0]+" "+op1+" "+or.toString();
                break;
            case "AND":
                Integer and = op2&op3;
                instruction = parts[0]+" "+op1+" "+and.toString();
                break;
            case "EX-OR":
                Integer xor = op2^op3;
                instruction = parts[0]+" "+op1+" "+xor.toString();
                break;
            case "LOAD":
                Integer memAddress = op2 + op3;
                instruction = parts[0]+" "+op1+" "+memAddress.toString();
                break;
            case "STORE":
                Integer memAddressStore = op2 + op3;
                instruction = parts[0]+" "+op1+" "+memAddressStore.toString();
                break;
        }
//        System.out.println("Execute "+instruction);
        return instruction;
    }

    /**
     * Method to get value from regsiters
     * @param Register
     * @param regName
     * @return Value of register
     */
    public static String getValueFromRegister(HashMap<String,Integer> Register,String regName){
        Integer value = Register.get(regName);
        return value.toString();
    }

    /**
     * Method to decode three op instruction
     * @param hashMap
     * @param instruction
     * @return decoded instruction
     */
    public static String decodeThreeOpInstruction(HashMap<String,Integer> hashMap, String instruction){
        String[] parts = instruction.split("[,\\s]+");
        String instructionType = parts[0];
        String op1 = parts[1];
        String op2 = parts[2];
        String op3 = parts[3];
        switch (instructionType){
            case "ADD":
            case "SUB":
            case "MUL":
            case "AND":
            case "OR":
            case "EX-OR":
                instruction = instructionType +" "+ op1 + " "+getValueFromRegister(hashMap, op2)+
                        " "+getValueFromRegister(hashMap,op3);
//                System.out.println("Testing   -------"+instruction);
                break;
            case "LOAD":
                String numberOnly= op3.replaceAll("[^-?0-9]", "");
                instruction = instructionType+" "+op1+" "+getValueFromRegister(hashMap,op2)+
                        " "+numberOnly;
                break;
            case "STORE":
                String numberOnlyStore= op3.replaceAll("[^-?0-9]", "");
                instruction = instructionType+" "+getValueFromRegister(hashMap,op1)+" "+getValueFromRegister(hashMap,op2)
                        +" "+numberOnlyStore;
                break;
            default:
                break;
        }
        return  instruction;
    }


    /**
     * Method to establish flags for dependency
     * and also check dependency
     * @param ValidMap
     * @param instruction
     * @return
     */
    public static boolean checkDependency(HashMap<String, String> ValidMap, String instruction){
        String[] parts = instruction.split("[,\\s]+");
        switch (parts[0]){
            case "ADD":
            case "SUB":
            case "MUL":
            case "AND":
            case "OR":
            case "EX-OR":
                if((ValidMap.get(parts[2])).equalsIgnoreCase("INVALID")
                        || (ValidMap.get(parts[3])).equalsIgnoreCase("INVALID"))
                    return false;
                break;
            case "LOAD":
                if((ValidMap.get(parts[2])).equalsIgnoreCase("INVALID"))
                    return false;
                break;
            case "STORE":
                if((ValidMap.get(parts[1])).equalsIgnoreCase("INVALID"))
                    return false;
                break;
            case "MOVC":
                if(ValidMap.get(parts[1]).equalsIgnoreCase("INVALID"))
                    return  false;
                break;
            default:
                break;


        }
        return true;
    }

}
