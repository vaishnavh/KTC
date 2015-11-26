
public class GridSearch {

	public String[] etas;
	public String[] num_iterations;
	public String[] sigmas;


	public static void main(String[] args)  throws Exception{
		run(args);
	}

	public static void run(String[] args) throws Exception{
		boolean inputError = true;
		
		String[] num_iter_arr=null;
		String[] eta_arr=null;
		String[] sigma_arr=null;
		
		try {
			String Ts=args[2];
			System.out.println(Ts);
			Ts=Ts.replace("[","");
			Ts=Ts.replace("]","");
			num_iter_arr=Ts.split(",");

			String etas=args[5];
			etas=etas.replace("[","");
			etas=etas.replace("]","");
			eta_arr=etas.split(",");

			String sigmas=args[6];
			sigmas=sigmas.replace("[","");
			sigmas=sigmas.replace("]","");
			sigma_arr=sigmas.split(",");
			
			inputError = false;

		} catch(Exception e){
			if(inputError){
				String fileName = "run_grid_search.sh";
				System.err.println("Usage: "+fileName+" [training] [output] [T] [N] [K] [eta] [sigma] [I1] [I2] ... [IN] [Kernel1] [Kernel2] ... [kernelN] [test] [query]\n");
				System.err.println("Make sure the etas is in the format [0.1,0.01,0.001] and sigmas are in a similar format\n");
				e.printStackTrace();
			}
			else {
				throw e;
			}
		}
		System.out.println("Input is ok for grid search. Running grid search now");
		System.out.println("Running over iterations:"+num_iter_arr);
		System.out.println("Running over etas:"+eta_arr);
		System.out.println("Running over sigmas:"+sigma_arr);
		
		int best_iter_value=Integer.MIN_VALUE;
		double best_eta_value=Integer.MIN_VALUE;
		double best_sigma_value=Integer.MIN_VALUE;
		double best_value=Double.MAX_VALUE;
		
		
		String[] args_for_program=args;
		for(int i=0;i<num_iter_arr.length;i++){
			args_for_program[2]=num_iter_arr[i];
			for(int j=0;j<eta_arr.length;j++){
				args_for_program[5]=eta_arr[j];
				for(int k=0;k<sigma_arr.length;k++){
					args_for_program[6]=sigma_arr[k];
					double[][] result=KPTFSGD.run(args_for_program);
					int number_iteration=Integer.parseInt(num_iter_arr[i]);
					double value=result[number_iteration-1][3];
					if(value<best_value){
						best_iter_value=Integer.parseInt(num_iter_arr[i]);
						best_eta_value=Double.parseDouble(eta_arr[j]);
						best_sigma_value=Double.parseDouble(sigma_arr[k]);
						best_value=value;
					}
				}
			}
		}
		
		System.out.println("Best Value="+best_value);
		System.out.println("Best Iter="+best_iter_value);
		System.out.println("Best Eta Value="+best_eta_value);
		System.out.println("Best Sigma Value="+best_sigma_value);
	}

}
