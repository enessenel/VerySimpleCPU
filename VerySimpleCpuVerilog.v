
parameter [3:0] ADD=0;
parameter [3:0] ADDi=1;
parameter [3:0] NAND=2;
parameter [3:0] NANDi=3;
parameter [3:0] SRL=4;
parameter [3:0] SRLi=5;
parameter [3:0] LT=6;
parameter [3:0] LTi=7;
parameter [3:0] CP=8;
parameter [3:0] CPi=9;
parameter [3:0] CPI=10;
parameter [3:0] CPIi=11;
parameter [3:0] BZJ=12;
parameter [3:0] BZJi=13;
parameter [3:0] MUL=14;
parameter [3:0] MULi=15;
reg[31:0] A, ANext, B, BNext, PC = 0, PCNext, opcode, opcodeNext, nextLoc;
reg[31:0] instr, instrNext, starA, starANext, starB, starBNext, doubleStarB, doubleStarBNext;
reg[3:0] state, stateNext;

//Fetch Instruction -> State 0
//Analyze Instruction -> State 1
//Read A -> State 2
//Read B -> State 3
//Execute Instruction -> State 4
//Write Result -> State 5
//Update PC -> State 6

always @(posedge clk) begin
    PC <= PCNext;
	state <= stateNext;
	starA <= starANext;
	starB <= starBNext;
	instr <= instrNext;
end

always@(*)begin
	 
    stateNext = state;

    if(rst) begin
        wrEn = 0;
      addr_toRAM = 0;
      data_toRAM = 0;
		instrNext = 0;
		starANext = 0;
		starBNext = 0;
		stateNext = 0;
		PCNext = 0;
    end

    else begin
        if(state == 0) begin

            addr_toRAM = PC;
            wrEn = 0;
            stateNext = 1;
        end

        else if(state == 1) begin

            instrNext = data_fromRAM;
            A = (instrNext & 32'h0FFFC000) >> 14;
          
            addr_toRAM = A;
            stateNext = 2;
        end

        else if(state == 2) begin
				B = (instr & 32'h00003FFF);
            starANext = data_fromRAM;
            addr_toRAM = B;
            stateNext = 3;
        end

        else if(state == 3) begin

            starBNext = data_fromRAM;
            stateNext = 4;
        end

        else if(state == 4) begin
	opcode = (instr >> 28);
				B = (instr & 32'h00003FFF);
				A = (instr & 32'h0FFFC000) >> 14;
            case(opcode)

                ADD : begin

                    starANext = starA + starB;
                    stateNext = 5;
                end

                ADDi : begin

                    starANext = starA + B;
                    stateNext = 5;

                end

                NAND : begin

                    starANext = ~(starA & starB);
                    stateNext = 5;
                end

                NANDi : begin

                    starANext = ~(starA & B);
                    stateNext = 5;
                end

                SRL : begin

                    if (starB < 32) begin
                        starANext = starA >> starB;
                    end
                    else begin
                        starANext = starA << (starB-32);
                    end
                    stateNext = 5;
                end

                SRLi : begin

                    if (B < 32) begin
                        starANext = starA >> B;
                    end
                    else begin
                        starANext = starA << (B-32);
                    end
                    stateNext = 5;
                end

                LT : begin

                    if (starA < starB) begin
                        starANext = 1;
                    end
                    else begin
                        starANext = 0;
                    end
                    stateNext = 5;
                end

                LTi : begin

                    if (starA < B) begin
                        starANext = 1;
                    end
                    else begin
                        starANext = 0;
                    end
                    stateNext = 5;
                end

                CP : begin

                    starANext = starB;
                    stateNext = 5;
                end

                CPi : begin
                    starANext = B;
                    stateNext = 5;
                end

                CPI : begin

                    addr_toRAM = starB;
                    stateNext = 5;
                end

                CPIi : begin

                    stateNext = 5;

                end

                BZJ: begin
                    if (starB == 0) begin
                        PCNext = starA;
                    end
                    else begin
                        PCNext = PC + 1;
                    end

                    stateNext = 6;
                end

                BZJi: begin
                    if (B == 0) begin
                        PCNext = starA;
                    end
                    else begin
                        PCNext = starA + B;
                    end

                    stateNext = 6;
                end

                MUL: begin
                    starANext = starA * starB;
                    stateNext = 5;
                end

                MULi: begin
                    starANext = starA * B;
                    stateNext = 5;
                end
            endcase
        end

        if(state == 5) begin

            wrEn = 1;
            if (opcode == CPIi) begin

                addr_toRAM = starA;
                data_toRAM = starB;
            end

            else if (opcode == CPI) begin
                doubleStarB = data_fromRAM;
                addr_toRAM = A;
                data_toRAM = doubleStarB;
            end

            else begin

                addr_toRAM = A;
                data_toRAM = starA;
            end
            stateNext = 6;
        end

        if(state == 6)begin

            wrEn = 0;
            if(opcode == BZJ || opcode == BZJi) begin
                PCNext = PCNext;
            end
            else begin
                PCNext = PC + 1;
            end
            stateNext = 0;
        end
    end
end
