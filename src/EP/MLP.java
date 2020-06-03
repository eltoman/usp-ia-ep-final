package EP;

/*
 * MLP.java
 *
 * This class implements a multi layer perceptron network with
 * logistic activation functions and backpropagation algorithm.
 * 
 * @author Juliano Jinzenji Duque <julianojd@gmail.com>
 * @author Luiz Eduardo Virgilio da Silva <luizeduardovs@gmail.com>
 *
 * CSIM
 * Computing on Signals and Images on Medicine Group
 * University of Sao Paulo
 * Ribeirao Preto - SP - Brazil
 */

public class MLP {
    
    private int nInputs, nHidden, nOutput;  // numeros de neuronios em cada camada
    private double[/* i */] input, hidden, output;

    private double[/* j */][/* i */] weightL1,  // pesos entre a camada oculta e a de entrada
    weightL2;  // pesos entre a camada oculta e a de saida
    private double learningRate = 0.5;

    /** 
     * Cria uma nova instancia da rede MLP
     *
     * @param nInput numero de neuronios na camada de entrada
     * @param nHidden numero de neuronios na camada escondida
     * @param nOutput numero de neuronios na camada de saida
     */
    public MLP(int nInput, int nHidden, int nOutput, boolean randomWeights) {

        this.nInputs = nInput;
        this.nHidden = nHidden;
        this.nOutput = nOutput;

        input = new double[nInput+1];
        hidden = new double[nHidden+1];
        output = new double[nOutput+1];

        weightL1 = new double[nHidden+1][nInput+1];
        weightL2 = new double[nOutput+1][nHidden+1];

        // Initialize weigths
        
        if(randomWeights)
            generateRandomWeights();
        else
            generateZeroWeights();
    }

    public double[][] getWeightL1() {
        return weightL1;
    }
    
    public double[][] getWeightL2() {
        return weightL2;
    }

    /**
     * Set a taxa de Aprendizado
     */
    public void setLearningRate(double lr) {
        learningRate = lr;
    }


    /**
     * Inicializar pesos com valores no intervalo [-0.5,0.5[
     */
    private void generateRandomWeights() {
        
        for(int j=1; j<=nHidden; j++)
            for(int i=0; i<=nInputs; i++) {
                weightL1[j][i] = Math.random() - 0.5;
        }

        for(int j=1; j<=nOutput; j++)
            for(int i=0; i<=nHidden; i++) {
            	weightL2[j][i] = Math.random() - 0.5;
        }
    }

    /**
     * inicializa os pesos em zero
     */
    private void generateZeroWeights() {
        
        for(int j=1; j<=nHidden; j++)
            for(int i=0; i<=nInputs; i++) {
                weightL1[j][i] = 0.0;
        }

        for(int j=1; j<=nOutput; j++)
            for(int i=0; i<=nHidden; i++) {
                weightL2[j][i] = 0.0;
        }
    }


    /**
     * Treina a rede com os dados de entrada e os pesos sao ajustados
     * via backpropagation, considerando a saida esperada.
     */
    public double[] train(double[] pattern, double[] desiredOutput) {
        double[] output = passNet(pattern);
        backpropagation(desiredOutput);

        return output;
    }


    /**
     * Testa os dados de entrada na rede
     * Funcao de ativacao logistica
     */
    public double[] passNet(double[] pattern) {

        for(int i=0; i<nInputs; i++) {
            input[i+1] = pattern[i];
        }
        
        // setando bias
        input[0] = 1.0;
        hidden[0] = 1.0;

        // passando pela camada oculta
        for(int j=1; j<=nHidden; j++) {
            hidden[j] = 0.0;
            for(int i=0; i<=nInputs; i++) {
                hidden[j] += weightL1[j][i] * input[i];
            }
            hidden[j] = 1.0/(1.0+Math.exp(-hidden[j]));
        }
    
        // passando pela camada de saida
        for(int j=1; j<=nOutput; j++) {
            output[j] = 0.0;
            for(int i=0; i<=nHidden; i++) {
                output[j] += weightL2[j][i] * hidden[i];
       	    }
            output[j] = 1.0/(1+0+Math.exp(-output[j]));
        }

        return output;
    }


    /**
     * Esse metodo ajusta os pesos considerando o erro backpropagationThe desired
     * A saida desejada eh comparada com a ultima saida da rede e os pesos sao ajustados
     * usando a taxa de aprendizado escolhida
     */
    private void backpropagation(double[] desiredOutput) {

        double[] errorL2 = new double[nOutput+1];
        double[] errorL1 = new double[nHidden+1];
        double Esum = 0.0;

        for(int i=1; i<=nOutput; i++)  // Camada de erro 2
            errorL2[i] = output[i] * (1.0-output[i]) * (desiredOutput[i-1]-output[i]);
	    
               
        for(int i=0; i<=nHidden; i++) {  // camada de erro 1
            for(int j=1; j<=nOutput; j++)
                Esum += weightL2[j][i] * errorL2[j];

            errorL1[i] = hidden[i] * (1.0-hidden[i]) * Esum;
            Esum = 0.0;
        }
             
        for(int j=1; j<=nOutput; j++)
            for(int i=0; i<=nHidden; i++)
            	weightL2[j][i] += learningRate * errorL2[j] * hidden[i];
         
        for(int j=1; j<=nHidden; j++)
            for(int i=0; i<=nInputs; i++) 
                weightL1[j][i] += learningRate * errorL1[j] * input[i];
    }
    
}
