import java.io.FileNotFoundException;

public class VerySimpleCPU {

	// enumeration of instructions
	final static int ADD = 0;
	final static int ADDi = 1;
	final static int NAND = 2;
	final static int NANDi = 3;
	final static int SRL = 4;
	final static int SRLi = 5;
	final static int LT = 6;
	final static int LTi = 7;
	final static int CP = 8;
	final static int CPi = 9;
	final static int CPI = 10;
	final static int CPIi = 11;
	final static int BZJ = 12;
	final static int BZJi = 13;
	final static int MUL = 14;
	final static int MULi = 15;

	public static void main(String[] args) throws FileNotFoundException {

		int A, B, PC = 0, opcode, PCPrev=-1;
		long instr, starA, starB, doubleStarB;

		// Use Hardware class
		Hardware vsCPU = new Hardware(0, 0);
		vsCPU.setClockPeriod(1);
		// Set Memory Size for VerySimpleCPU
		vsCPU.setMemorySize(65536);
		// Load program to memory array
		vsCPU.loadProgram();

		vsCPU.resetWriteEnable();
		vsCPU.setAddress((short) 0);
		vsCPU.setDataIn(0);

		// Implement the Instructions of VerySimpleCPU
		while (true) {

			vsCPU.posedge(); // 1st cycle

			//to end program when it loops
			if(PCPrev == PC){

				vsCPU.dumpMemory();
				return;
			}
			PCPrev = PC;
			
			vsCPU.setAddress((short) PC);
			vsCPU.posedge(); 

			instr = vsCPU.getDataOut();
			opcode = (int) (instr >> 28);

			B = (int) (instr & 0x00003FFF);
			A = (int) (instr >> 14);
			A = (A & 0x00003FFF);

			switch (opcode) {

			case ADD:

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				// write A+B to A
				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starA + starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case ADDi:

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starA + B);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case NAND:

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(~(starA & starB));
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case NANDi:

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(~(starA & B));
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case SRL:

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);

				if (starB < 32)
					vsCPU.setDataIn(starA >> starB);
				else
					vsCPU.setDataIn(starA << (starB-32));

				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case SRLi:

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				if (B < 32)
					vsCPU.setDataIn(starA >> B);

				else
					vsCPU.setDataIn(starA << (B-32));

				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case LT:

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);

				if (starA < starB)
					vsCPU.setDataIn(1);
				else
					vsCPU.setDataIn(0);

				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case LTi:

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);

				if (starA < B)
					vsCPU.setDataIn(1);
				else
					vsCPU.setDataIn(0);

				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case CP:

				//Read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				// WriteEnable
				PC = PC + 1;
				break;

			case CPi:

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(B);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case CPI:

				// read *B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read **B
				vsCPU.setAddress((short) starB);
				vsCPU.posedge();
				doubleStarB = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(doubleStarB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case CPIi:

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				vsCPU.setAddress((short) starA);	
				vsCPU.setDataIn(starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case BZJ:

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				if (starB == 0)
					PC = (int) starA;
				else
					PC = PC + 1;

				break;

			case BZJi:

				vsCPU.setAddress((short) A);
				vsCPU.posedge(); 
				starA = vsCPU.getDataOut();

				PC = (int) (starA + B);
				break;

			case MUL:

				// read B
				vsCPU.setAddress((short) B);
				vsCPU.posedge();
				starB = vsCPU.getDataOut();

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				// write A*B to A
				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starA * starB);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;

			case MULi:

				// read A
				vsCPU.setAddress((short) A);
				vsCPU.posedge();
				starA = vsCPU.getDataOut();

				vsCPU.setAddress((short) A);
				vsCPU.setDataIn(starA * B);
				vsCPU.setWriteEnable();
				vsCPU.posedge();

				vsCPU.resetWriteEnable();
				PC = PC + 1;
				break;
			}
		}
	}
}
