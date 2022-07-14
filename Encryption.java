public class Encryption
{
	public static String Encrypt(String toEncrypt)
	{

		int Step1, Step2, Step3, Step7, StepA;
		Integer Step4;

		char SnglChar, Step6, Step9;

		String OutputStr, TempStr;
		String Step5;
		OutputStr = "";

		for (int x = 0; x < toEncrypt.length(); x++)
		{
			SnglChar = toEncrypt.charAt(x);

			Step1 = (int)SnglChar;
		    Step2 = Step1 * Step1;
		    Step3 = Step2 * Step2;
		    Step4 = new Integer(Step3 + 91);

		    Step5 = Step4.toString();
			StepA = Step5.length() + 91;
			Step6 = (char)StepA;

			OutputStr = OutputStr + Step6;

		    for (int i = 0; i < Step5.length(); i++)
		    {
				TempStr = Step5.substring(i, i + 1);

				if (TempStr != "")
				{
					Step7 = Integer.parseInt(TempStr) + 65;
					Step9 = (char)Step7;

					OutputStr = OutputStr + Step9;
				}

				TempStr = "";
			}
		}
		return OutputStr;
	}

	public static String Decrypt(String toDecrypt)
	{
		int EncLen, Start, End, Test;
		int StepA;
		int x = 0;
		double Step2, Step3;

		String OutputStr, Temp;
		char SnglChar, charCurr;

		OutputStr = "";
		Test = 0;

		while (x < toDecrypt.length())
		{
			SnglChar = toDecrypt.charAt(x);
			EncLen = (int)SnglChar - 91;

			Start = x + 1;
			End = x + EncLen;

			StepA = 0;
			Temp = "";

			for (int i = Start; i <= End; i++)
			{
				charCurr = toDecrypt.charAt(i);

				Test = (int)charCurr - 65;
				Temp = Temp + Test;
			}

			StepA = Integer.parseInt(Temp);
			Step3 = Math.sqrt(StepA);
			Step2 = Math.sqrt(Step3);

			x = End + 1;

			OutputStr = OutputStr + (char)Step2;
		}

		return OutputStr;
	}
}
