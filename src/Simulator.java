/**
 * Created by Aamir on 11/12/16.
 */
import java.io.File;
import java.util.*;

public class Simulator {

    private static Integer programCounter = 4000;
    private static ArrayList<String> instructionList = new ArrayList<String>();
    private static ArrayList<String> displayList = new ArrayList<String>();

    private static Integer[] memoryArray = new Integer[4000];
    public static HashMap<String, Integer> REGISTER = new HashMap<>();
    public static HashMap<String,String> VALIDBITS = new HashMap<>();
    private static boolean instructionFetched, instructionDecoded, instructionExecuted,
                           execute2done, memoryStageDone, writebackDone;
    private static boolean haltInstruction = false;
    private static boolean branchTaken = false;
    private static boolean jumpInstruction = false;
    private static boolean invalidPC = false;
    private static boolean zeroFlag = false;
    private static Integer updatedPC=0;
    private static String[] last2Instructions = new String[2];
    private static String lastInstructionFetched;

    private static String f_dLatch;
    private static String d_exLatch;
    private static String ex_ex2Latch;  //Latches for flow of instructions b/w stages
    private static String ex_memLatch;
    private static String mem_wblatch;

    private  static String fetch_instruction;
    private  static String decode_instruction;
    private  static String execute_instruction;  //Variables to save state/instruction in pipeline after
    private static String execute_instruction2;  // every cycle
    private  static String memory_instruction;
    private  static String wb_instruction;

    private static String[] invalid2Register= new String[2];
    private static String   invalidLoad;
    private static String invalidStore;
    private static String invalidMOVC;

    private static String lastDecodedInstruction;


    /**
     * Initializes Simulator
     * @param fileName
     */
    public static void init(File fileName){
        programCounter=4000;
        System.out.printf("--Program counter set to 4000-");

        FileProcessor fp = new FileProcessor(fileName,programCounter);

        instructionList = fp.OpenFile();
        for(int i=0;i<memoryArray.length;i++){
            memoryArray[i]=null;
        }

        //Initializing Invalid Register Array for Add, Sub, Mul, or, ex-or, and
        invalid2Register[0] = null;
        invalid2Register[1] = null;


        //Initializing 16 Registers
        REGISTER.put("R0",0);
        REGISTER.put("R1",0);
        REGISTER.put("R2",0);
        REGISTER.put("R3",0);
        REGISTER.put("R4",0);
        REGISTER.put("R5",0);
        REGISTER.put("R6",0);
        REGISTER.put("R7",0);
        REGISTER.put("R8",0);
        REGISTER.put("R9",0);
        REGISTER.put("R10",0);
        REGISTER.put("R11",0);
        REGISTER.put("R12",0);
        REGISTER.put("R13",0);
        REGISTER.put("R14",0);
        REGISTER.put("R15",0);
        System.out.println("\n\n--Registers R0 to R15 initialized--");

        //Initializing all registers to valid flags
        VALIDBITS.put("R0","VALID");
        VALIDBITS.put("R1","VALID");
        VALIDBITS.put("R2","VALID");
        VALIDBITS.put("R3","VALID");
        VALIDBITS.put("R4","VALID");
        VALIDBITS.put("R5","VALID");
        VALIDBITS.put("R6","VALID");    //Assign validity to corresponding Architectural Registers
        VALIDBITS.put("R7","VALID");    // Helps with stalling on dependency.
        VALIDBITS.put("R8","VALID");
        VALIDBITS.put("R9","VALID");
        VALIDBITS.put("R10","VALID");
        VALIDBITS.put("R11","VALID");
        VALIDBITS.put("R12","VALID");
        VALIDBITS.put("R13","VALID");
        VALIDBITS.put("R14","VALID");
        VALIDBITS.put("R15","VALID");
        System.out.println("\n\n--Register Validity flags initialized");

        last2Instructions[0]=null;
        last2Instructions[1]=null;

        //Resetting flags
        instructionFetched=instructionDecoded=memoryStageDone=writebackDone=invalidPC=false;
        branchTaken=jumpInstruction=haltInstruction=zeroFlag=instructionExecuted=false;
        System.out.println("\n--Flags set--");
        System.out.println("\n--Simulator Initialized Successfully--");
    }

    /**
     * Displays Simulator
     */
    public static void display(){
        System.out.println(printInOrder("CYCLE")+printInOrder("FETCH")+printInOrder("DECODE")
                +printInOrder("EXECUTE-1")+printInOrder("EXECUTE-2")+printInOrder("MEMORY")+printInOrder("WRITEBACK"));

        for(int i=0;i<displayList.size();i++){
            System.out.println(displayList.get(i));
        }

        System.out.println("\nRegister Content:");
        System.out.println("R0:"+REGISTER.get("R0")+"   R1:"+REGISTER.get("R1")+"   R2:"+REGISTER.get("R2")+
                            "   R3:"+REGISTER.get("R3")+"   R4:"+REGISTER.get("R4")+ "   R5:"+REGISTER.get("R5")+
                            "   R6:"+REGISTER.get("R6")+"   R7:"+REGISTER.get("R7")+ "   R8:"+REGISTER.get("R8")+
                            "   R9:"+REGISTER.get("R9")+"   R10:"+REGISTER.get("R10")+ "   R11:"+REGISTER.get("R11")+
                            "   R12:"+REGISTER.get("R12")+"   R13:"+REGISTER.get("R13")+ "   R14:"+REGISTER.get("R14")+
                            "   R15:"+REGISTER.get("R15"));

        for(int i=0;i<memoryArray.length;i++){
            if(memoryArray[i]!=null){
                System.out.println("Meem Location "+i+" "+memoryArray[i]);
            }
        }

    }

    /**
     * Simulates simulator
     * @param numberOfCycles number of cycles to simulate
     */
    public static void simulate(int numberOfCycles){
        int cycles =0;
        for(int i=0; i<numberOfCycles;i++){
            if(invalidPC || (haltInstruction&&instructionFetched&&instructionDecoded&&instructionExecuted
            &&execute2done&&memoryStageDone&&writebackDone)){
                break;
            }
            doWriteBack();
            doMemory();
            doExecute2();
            doExecute();
            doDecode();
            doFetch();
            Integer k=i+1;
            displayList.add(printInOrder(k.toString())+printInOrder(fetch_instruction)+printInOrder(decode_instruction)
                    +printInOrder(execute_instruction)+printInOrder(execute_instruction2)+printInOrder(memory_instruction)
                    +printInOrder(wb_instruction));
        }
        if(cycles != numberOfCycles && (invalidPC||haltInstruction)){
            display();
            if(haltInstruction){
                System.out.println("Halt instruction, simulation ended");
            } else if(invalidPC){
                System.out.println("Invalid PC encountered, simulation ended");
            }
            System.exit(1);
        }
    }

    /**
     * writeback stage of simulator
     */
    public static void doWriteBack(){
        String instruction=mem_wblatch;
        mem_wblatch=null;
        if (writebackDone) {
            wb_instruction = "Done";
            return;
        }

        if(instruction==null){
            if(memoryStageDone){
                writebackDone=true;
                wb_instruction="Done";
            } else {
                wb_instruction="Stall";
            }
            return;
        }
        if(instruction.equalsIgnoreCase("HALT")){
            wb_instruction=instruction;
            writebackDone=true;
            return;
        }

        String instructionType = instruction.split("[,\\s]+")[0];
        switch (instructionType) {
            case "ADD":
            case "MUL":
            case "SUB":
            case "OR":
            case "AND":
            case "LOAD":
            case "EX-OR":
                String[] parts = instruction.split("[,\\s]+");
                REGISTER.put(parts[1], Integer.parseInt(parts[2]));
                wb_instruction = instruction;
                break;
            case "STORE":
                wb_instruction = instruction;
                break;
            case "MOVC":
                String[] partsMovc = instruction.split("[,\\s]+");
                REGISTER.put(partsMovc[1], Integer.parseInt(partsMovc[2]));
                wb_instruction = instruction;
                break;
            default:
                break;
        }

    }


    /**
     * Memory stage of simulator
     */
    public static void doMemory(){
        String instruction=ex_memLatch;
        ex_memLatch=null;
        if (instruction==null){
            if(execute2done){
                memoryStageDone=true;
                memory_instruction="Done";
            } else {
                memory_instruction="Stall";
            }
            return;
        }

        if(instruction.equalsIgnoreCase("HALT")){
            memoryStageDone=true;
            memory_instruction=instruction;
            mem_wblatch=instruction;
        } else{
            String instructionType = instruction.split("[,\\s]+")[0];
            switch (instructionType){
                case "ADD":
                case "MUL":
                case "SUB":
                case "OR":
                case "AND":
                case "EX-OR":
                case "MOVC":
                    mem_wblatch=instruction;
                    memory_instruction=instruction;
                    break;
                case "LOAD":
                    String[] parts = instruction.split("[,\\s]+");
                    Integer address = Integer.parseInt(parts[2]);
                    Integer result = memoryArray[address];
                    instruction = parts[0]+" "+parts[1]+ " "+result.toString();
                    memory_instruction=instruction;
                    mem_wblatch=instruction;
                    ex_memLatch=null;
                    REGISTER.replace(parts[1], Integer.parseInt(parts[2]));
                    VALIDBITS.replace(invalidLoad,"VALID");
                    break;
                case "STORE":
                    String[] partsStore = instruction.split("[,\\s]+");
                    memoryArray[Integer.parseInt(partsStore[2])]=Integer.parseInt(partsStore[1]);
                    memory_instruction=instruction;
                    mem_wblatch=instruction;
                    ex_memLatch=null;
                    VALIDBITS.replace(invalidStore,"VALID");
                    break;
                default:
                    break;

            }
            memory_instruction=instruction;
            mem_wblatch=instruction;
        }

    }

    /**
     * 2nd stage of functional unit
     */
    public static void doExecute2(){
        String instruction = ex_ex2Latch;
        ex_ex2Latch=null;
        if(instruction==null){
            if(instructionExecuted){
                execute2done=true;
                execute_instruction2="Done";
            } else{
                execute_instruction2="Stall";
            }
            return;
        }

        if(instruction.equalsIgnoreCase("HALT")){
            execute_instruction2=instruction;
            ex_memLatch=instruction;
            execute2done=true;  //new
        } else {
            String[] parts = instruction.split("[,\\s]+");
            switch (parts[0]){
                case "ADD":
                case "SUB":
                case "MUL":
                case "AND":
                case "OR":
                case "EX-OR":
                    VALIDBITS.replace(invalid2Register[0],"VALID");
                    VALIDBITS.replace(invalid2Register[1],"VALID");
                    break;
                case "MOVC":
                    VALIDBITS.replace(invalidMOVC,"VALID");

                    break;
                default:
                    break;
            }
            execute_instruction2=instruction;
            ex_memLatch=instruction;
        }

    }

    /**
     * 1st stage of functional unit
     */
    public static void doExecute(){
        if (instructionExecuted) {
            execute_instruction="Done";
        }
        String instruction = d_exLatch;
        d_exLatch = null;
        if(instruction==null){
            if(instructionDecoded){
                instructionExecuted=true;
                execute_instruction="Done";
            } else {
                execute_instruction="Stall";
            }
            return;
        }
        if(instruction.equalsIgnoreCase("HALT")){
            execute_instruction=instruction;
            ex_ex2Latch=instruction;
            instructionExecuted = true;
        } else {
            String instructionType = instruction.split("[,\\s]+")[0];
            switch (instructionType){
                case "ADD":
                case"SUB":
                case "MUL":
                case "AND":
                case "OR":
                case "EX-OR":
                    instruction= Helper.executeThreeOpInstructionLatch(REGISTER, instruction);
                    String[] partsIFU = instruction.split("[,\\s]+");
                    REGISTER.replace(partsIFU[1], Integer.parseInt(partsIFU[2]));
                    execute_instruction=instruction;
                    ex_ex2Latch=instruction;
                    break;
                case "MOVC":
                    String[] partsMOVC = instruction.split("[,\\s]+");
//                    VALIDBITS.replace(invalidMOVC,"VALID");
                    REGISTER.replace(partsMOVC[1], Integer.parseInt(partsMOVC[2]));
                    execute_instruction=instruction;
                    ex_ex2Latch=instruction;
                    break;
                case "STORE":
                    instruction=Helper.decodeThreeOpInstruction(REGISTER,instruction);
                    instruction=Helper.executeThreeOpInstructionLatch(REGISTER,instruction);
                    String[] partsSTORE = instruction.split("[,\\s]+");
//                    memoryArray[Integer.parseInt(partsSTORE[2])]=Integer.parseInt(partsSTORE[1]);
                    execute_instruction=instruction;
                    ex_ex2Latch=instruction;
                    break;
                case "LOAD":
                    instruction=Helper.decodeThreeOpInstruction(REGISTER,instruction);
                    instruction=Helper.executeThreeOpInstructionLatch(REGISTER,instruction);
                    String[] partsLOAD = instruction.split("[,\\s]+");
//                    REGISTER.put(partsLOAD[1],memoryArray[Integer.parseInt(partsLOAD[2])]);
                    execute_instruction=instruction;
                    ex_ex2Latch=instruction;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Decode stage of Simulator
     */
    public static void doDecode(){
        if(instructionDecoded) {
            decode_instruction="Done";
        }else {
            String instruction = f_dLatch;
            f_dLatch=null;
            if (instruction == null) {
                if (instructionFetched) {
                    instructionDecoded = true;
                    decode_instruction="Done";  //new c
                } else {
                    decode_instruction="Stall";
                }
                return;
            }
            if(haltInstruction){
                decode_instruction="Done";
            }else if (instruction.equalsIgnoreCase("HALT")) {
                instructionDecoded = true;
                haltInstruction = true;
                decode_instruction=instruction;
                d_exLatch = instruction;
            } else {
                if (!Helper.checkDependency(VALIDBITS,instruction)) {
                    decode_instruction = instruction;

                } else {
                    String decodedInstrution = null;
                    int category = Helper.getInstructionCategory(instruction);
                    switch (category) {
                        case 1:
                            decodedInstrution = instruction;
                            decode_instruction=instruction;
                            d_exLatch=decodedInstrution;
//                            lastDecodedInstruction=instruction; //new c, up and down
                            break;
                        case 2:
                            String[] partsMOVC = instruction.split("[,\\s]+");
                            decodedInstrution = Helper.decodeTwoOpInstruction(REGISTER, instruction);
                            decode_instruction=instruction;
                            d_exLatch=decodedInstrution;
//                            VALIDBITS.replace(partsMOVC[1],"INVALID");
//                            invalidMOVC=partsMOVC[1];
//                            f_dLatch=instruction;
//                            lastDecodedInstruction=instruction;
                            break;
                        case 3:
                            String[] decode_parts = instruction.split("[,\\s]+");
                            if(decode_parts[0].equalsIgnoreCase("LOAD") || decode_parts[0].equalsIgnoreCase("STORE")){
                                if(decode_parts[0].equalsIgnoreCase("STORE")){
                                    VALIDBITS.replace(decode_parts[1], "INVALID");
                                    invalidStore = decode_parts[1];
                                    f_dLatch = instruction;
                                } else {
                                    VALIDBITS.replace(decode_parts[2], "INVALID");
                                    invalidLoad = decode_parts[2];
                                    f_dLatch = instruction;
                                }
                                decodedInstrution = instruction;
                                decode_instruction=instruction;
//                                System.out.println("After Decode --: "+decodedInstrution);
                                d_exLatch=decodedInstrution;
//                                System.out.println("After Decode --: " + decodedInstrution);
                            } else {
                                String decode_action = decode_parts[0];
                                switch (decode_action) {
                                    case "ADD":
                                    case "SUB":
                                    case "MUL":
                                    case "AND":
                                    case "OR":
                                    case "EX-OR":
                                        VALIDBITS.replace(decode_parts[2], "INVALID");
                                        invalid2Register[0] = decode_parts[2];
                                        VALIDBITS.replace(decode_parts[3], "INVALID");
                                        invalid2Register[1] = decode_parts[3];
                                        f_dLatch = instruction;
                                        break;
                                    default:
                                        break;
                                }
                                decodedInstrution = Helper.decodeThreeOpInstruction(REGISTER, instruction);
                                decode_instruction = instruction;
                                d_exLatch = decodedInstrution;
                                break;
                            }
                        default:
                            break;

                    }
                }

            }
        }

    }

    /**
     * Fetch stage of simulator
     */
    public static void doFetch(){
        if(instructionFetched){
            fetch_instruction="Done";
        } else {
            if(f_dLatch!=null) {
                fetch_instruction=lastInstructionFetched;
            } else if(programCounter==instructionList.size()){
                    instructionFetched=true;
                    if(!instructionDecoded){
                        fetch_instruction = lastInstructionFetched;
                    } else {
                        fetch_instruction="Done";
                    }
            } else if(programCounter>instructionList.size()-1){
                    System.out.println("Invalid PC Value");
                    invalidPC=true;
            } else {
                    String instruction = instructionList.get(programCounter);
//                    System.out.println("Test "+instruction);
                    programCounter++;
                    f_dLatch = instruction;

                    if(haltInstruction){
                        instructionFetched=true;
                        instruction=f_dLatch;

//                        instruction="DONE";
                        f_dLatch=instruction;
                    }
                    lastInstructionFetched = instruction;
                    fetch_instruction = instruction;
            }
        }
    }


    /**
     * Method to jsutify a string
     * helps in printing content to screen
     * @param s1 string to be justified for printing.
     * @return justified string
     */
    public static String printInOrder(String s1){
        if(s1==null){
            s1="null";
        }
        int x = s1.length();
        int y = (24-x)/2;
        return repeat(" ",y)+s1+repeat(" ",y);
    }

    /**
     * Method to repeat a character or string,
     * helps in printing content to screen
     * @param str string to be repeated
     * @param num number of times it's to be repeated
     * @return repeated string
     */
    public static String repeat(String str, int num){
        String rep = str;
        for(int i=0;i<num;i++){
            rep+=str;
        }
        return rep;
    }

}
