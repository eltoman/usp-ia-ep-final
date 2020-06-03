package EP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import Interface.DataSet;
import Models.TestSet;
import Models.TrainingSet;
import Models.ValidationSet;
import Models.Weight;
 
/**
 * Algorítmo: Learning Vector Quantization.
 * 
 * Realiza o treinamento dos dados, depois faz a validação e então
 * computa o resultado de acordo com o batimento dos testes.
 *
 */
public class LVQ
{
 
	private static Random _random = new Random();

	private DataSet _currentInput;
	
	private boolean firstTraining = true;
	private int _epoca;
	private int _inputNeurons;
	private int _outputNeurons;

	private double[][] _lastWeights;
	private double[][] _currentWeights;
	public double[][] get_currentWeights() {
		return _currentWeights;
	}

	public void set_currentWeights(double[][] _currentWeights) {
		this._currentWeights = _currentWeights;
	}

	private int currentValidationDeltaError;
	private int lastValidationDeltaError;
	private int testDeltaError;
	private BigDecimal _learnrate;
	private BigDecimal _firstLearnRate;
	private int _quantidadeNeuronios;
	private String _tipoInicializacaoPesos;
	private boolean _stopTraining = false;	
	public int validationCount = 0;
	public int epocaDeParada = 0;
	
	public int getTestDeltaError() {
		return testDeltaError;
	}

	public void setTestDeltaError(int testDeltaError) {
		this.testDeltaError = testDeltaError;
	}
	
	public DataSet getCurrentInput()
	{
		return _currentInput;
	}
 
	public void setCurrentInput(DataSet dataSet)
	{
		_currentInput = dataSet;
	}

	public void setCurrentInputNeurons(double[] neurons)
	{
		_currentInput.setDataSet(neurons);
	}

	public ArrayList<TrainingSet> get_trainingSets() {
		return _trainingSets;
	}

	public void set_trainingSets(ArrayList<TrainingSet> _trainingSets) {
		this._trainingSets = _trainingSets;
	}

	public ArrayList<ValidationSet> get_validationSets() {
		return _validationSets;
	}

	public void set_validationSets(ArrayList<ValidationSet> _validationSets) {
		this._validationSets = _validationSets;
	}

	public ArrayList<TestSet> get_testSets() {
		return _testSets;
	}

	public void set_testSets(ArrayList<TestSet> _testSets) {
		this._testSets = _testSets;
	}
	
	private ArrayList<TrainingSet> _trainingSets;
	private ArrayList<ValidationSet>  _validationSets;
	private ArrayList<TestSet>  _testSets;
 
	/**
	 * Construtor principal.
	 * 
	 * @param inputNeurons neurônios de entrada
	 * @param outputNeurons neurônios de saída
	 * @param learnrate taxa de aprendizado
	 * @param epoca 
	 * @param qtdNeuroniosCadaClasse quantidade de neurônios por classe
	 * @param tipoInicializacaoPeso tipo de inicialização dos pesos (ALEATÓRIO ou A)
	 */
	public LVQ(int inputNeurons, int outputNeurons, BigDecimal learnrate, int epoca, int qtdNeuroniosCadaClasse, String tipoInicializacaoPeso){
		
		_trainingSets = new ArrayList<TrainingSet>();
		_validationSets = new ArrayList<ValidationSet>();
		_testSets = new ArrayList<TestSet>();
		_inputNeurons = inputNeurons;
		_outputNeurons = outputNeurons;
		_learnrate = learnrate;
		_firstLearnRate = learnrate;
		_epoca = epoca;
		_currentWeights = new double[_outputNeurons][_inputNeurons];
		_quantidadeNeuronios = qtdNeuroniosCadaClasse;
		_tipoInicializacaoPesos = tipoInicializacaoPeso;
		this.initWeights();
		
	}
 
	/**
	 * Adiciona uma linha (trainingSet) do arquivo de entrada no vetor global
	 * de training sets.
	 * 
	 * @param trainingSet Linha do arquivo de entrada
	 * @param category Classe esperada da linha
	 */
	public void addTrainingSet(double[] trainingSet, int category){
		
		TrainingSet tSet = new TrainingSet();
		tSet.setDataSet(trainingSet);
		tSet.setExpectedCategory((category));
		
		_trainingSets.add(tSet);
		
	}

	/**
	 * Adiciona uma linha do conjunto de testes ao vetor global de testes.
	 * 
	 * @param testSet linha do arquivo de teste
	 * @param category classe esperada da linha do arquivo de teste
	 */
	public void addTestSet(double[] testSet, int category){
		
		TestSet tSet = new TestSet();
		tSet.setDataSet(testSet);
		tSet.setExpectedCategory((category));
		
		_testSets.add(tSet);
		
	}
	
	/**
	 * Adiciona uma linha do conjunto de validação ao vetor global de validação.
	 * 
	 * @param conjuntoValidacao linha do conjunto de validação
	 * @param category classe esperada
	 */
	public void addValidationSet(double[] conjuntoValidacao, int category){
		
		ValidationSet tSet = new ValidationSet();
		tSet.setDataSet(conjuntoValidacao);
		tSet.setExpectedCategory((category));
		
		_validationSets.add(tSet);
		
	}	
	
	/**
	 * Calcula a distancia euclidiana entre um vetor e o respectivo vetor dentro da matriz de pesos.
	 * @param index classe a ser testada
	 * @param dataSet linha de entrada
	 * @return
	 */
	public double Euclidiana(int index, double[] dataSet){
		 
		double sum = 0;
		  
		  for(int i = 0; i < _inputNeurons; i++){
			  sum += Math.pow(((dataSet[i]) - _currentWeights[index][i]), 2);
			  
		  }		  
		  return Math.sqrt(Math.abs(sum));
		  
	}

	/**
	 * Método de treinamento de dados.
	 * Utiliza o algorítmo LVQ para treinar o conjunto de treinamento e os neurônios protótipos.
	 */
	public void trainIteration(){
		
		int counter = 0; //contador de iteraçoes
		int divide = 100; //divisor da range
		int part = _epoca/divide; //particiona a range para criar as épocas
		int x = 1; //multiplicador da partição, utilizado para gerar uma época
		int fimEpoca = (part * x); //limite de uma determinada época
		
		boolean beginning = true;

		System.out.println("Iniciando para a range: " + 0 +" ate " + _epoca +".");
		
		for (int w = 0; w <= _epoca; w++) {
			
			if(beginning) {
				System.out.println(" ");
				System.out.println("Inicio "+fimEpoca+" Epocas");
				beginning = false;
			}
			
			 /* Condição de parada da época */
			if (counter == fimEpoca) {	
				
				/* Atualiza a classe dos vetores de entrada */
				updateInputDataCategories();
				
				/* Calcula o erro do vetor de validação para a matriz de pesos gerada */
				iterateValidation();
				
				System.out.println("Epoca: "+ counter + " taxa erro:  " + (100-(validationCount * 100) / _validationSets.size() + "%" ));
				System.out.println("Fim "+counter+" Epocas");
				
				/* Valida o conjunto treinado com o vetor de validação */
				validateTraining();
				
				if(_stopTraining) {
					epocaDeParada = fimEpoca;
					return;
				}
				
				beginning = true;
				
				/* Re-inicializa os pesos */
				initWeights();	
				
				firstTraining = false;		
				
				x += 1;
				
				fimEpoca = (part * x);
			}
			
			Iterator<TrainingSet> it = _trainingSets.iterator();
			
			while (it.hasNext()) {
				
				setCurrentInput((TrainingSet) it.next());
				
				/* Classe para o vetor de input */
				int j = minDistanceIndex();

				/* Caso a classificação obtida seja a classificação desejada, aproxima o peso do vetor input.
				 * Caso contrário, afasta. */
				if (j == _currentInput.getExpectedCategory()) {
					_currentWeights[j] = sumVectors(_currentWeights[j], (multiplyVector(_learnrate, (subtractVectors(_currentInput.getDataSet(), _currentWeights[j])))));
				} else {
					_currentWeights[j] = subtractVectors(_currentWeights[j], (multiplyVector(_learnrate, (subtractVectors(_currentInput.getDataSet(), _currentWeights[j])))));
				}
				
			}
			
			/*
			 * Reduz a taxa de aprendizado
			 */
			updateLearningRate(w, _epoca);
			
			counter++;
			
		}
		
	}

	/**
	 * Percorre o conjunto de testes e calcula a variação entre a quantidade de acertos e erros de acordo
	 * com a matriz de pesos gerada pelo conjunto de treinamento.
	 */
	public void testIteration(){

		Iterator<TestSet> it = _testSets.iterator();
		
		while(it.hasNext()) {
			
			TestSet ts = it.next();
			
			setCurrentInput(ts);
			
			ts.setCurrentCategory(minDistanceIndex());
			
			testDeltaError += Math.abs((ts.getExpectedCategory() - ts.getCurrentCategory()));
			
 		}
		
	}	
	
	/**
	 * Percorre o conjunto de validação e, para cada linha, calcula a classificação da linha
	 * de acordo com a matriz de vetores prototipos gerada pelo conjunto de treinamento.
	 * 
	 * Após isso, computa a quantidade de erros e acertos e calcula o erro total do algorítmo.
	 */
	public void iterateValidation(){
		
		Iterator<ValidationSet> it = _validationSets.iterator();
		
		currentValidationDeltaError = 0;
		validationCount = 0;
		
		while(it.hasNext()) {
			
			ValidationSet vs = it.next();
			
			setCurrentInput(vs);
			
			vs.setCurrentCategory(minDistanceIndex());
			
			currentValidationDeltaError += Math.abs((vs.getExpectedCategory() - vs.getCurrentCategory()));
			if(vs.getExpectedCategory() == vs.getCurrentCategory()) validationCount ++;
			
 		}
		
	}
	
	/**
	 * Verifica se o erro total do conjunto de validação, para a matriz de pesos, é maior do
	 * que o erro anterior. Se for, encerra o treinamento dos dados.
	 */
	public void validateTraining() {
		
		if(currentValidationDeltaError > lastValidationDeltaError && !firstTraining) {
			
			_currentWeights = _lastWeights;
			_stopTraining = true;
			
		}else{
			
			lastValidationDeltaError = currentValidationDeltaError;
			_lastWeights = _currentWeights;
			
		}
		
	}
	
	/**
	 * Atualiza a classificação dos neurônios de entrada.
	 */
	public void updateInputDataCategories(){
		
		Iterator<ValidationSet> itv = _validationSets.iterator();
		
		while(itv.hasNext()) {
			
			ValidationSet vs = itv.next();
			
			setCurrentInput(vs);
			
			vs.setCurrentCategory(minDistanceIndex());

		}
		
	}
	
	/**
	 * Restaura a matriz de pesos para sua condição mais próxima do melhor caso.
	 */
	public void restoreWeights() {
		
		Iterator<ValidationSet> it = _validationSets.iterator();
		
		while(it.hasNext()) {
			
			ValidationSet vs = it.next();
			
			Weight w = new Weight();
			
			w.setCurrentWeight(vs.getWeight().getLastWeight());
			w.setLastWeight(vs.getWeight().getLastWeight());
			
			vs.setWeight(w);
			
		}
		
	}
	
	/**
	 * Soma dois vetores.
	 * 
	 * @param x
	 * @param y
	 * @return vetor soma
	 */
	private double[] sumVectors(double[] x, double[] y) {		
		double[] result = new double[y.length];		
		for(int i = 0; i < result.length; i++) {			
			result[i] = x[i] + y[i];		
		}		
		return result;		
	}

	/**
	 * 
	 * Multiplica um vetor por uma taxa.
	 * 
	 * @param multiplier multiplicador
	 * @param vector vetor
	 * @return vetor multiplicado
	 */
	 private double[] multiplyVector(BigDecimal multiplier, double[] vector) {
		  
		 double[] result = new double[vector.length];
		  
		  for(int i = 0; i < result.length; i++) {
			  
			  BigDecimal mult = multiplier.multiply(new BigDecimal(String.valueOf(vector[i])));
		   
			  result[i] = mult.doubleValue();
		   
		  }
		  
		  return result;
		  
	 }
	
	 /**
	  * Subtração de dois vetores.
	  * 
	  * @param x vetor a ser reduzido
	  * @param y vetor que vai ser retirado do vetor x
	  * @return x[]-y[]
	  */
	private double[] subtractVectors(double[] x, double[] y) {
		
		double[] result = new double[y.length];
		
		for(int i = 0; i < result.length; i++) {
			
			result[i] = x[i] - y[i];
			
		}
		
		return result;
		
	}

	/**
	 * Retorna a classe do vetor de parâmetros que seja mais próximo do vetor de entrada atual
	 * @return classe
	 */
	public int minDistanceIndex() {
		
		double value;
		double oldValue = Double.MAX_VALUE;
		int index = 0;
		
		for (int i = 0; i < _outputNeurons; i++) {
			
			value = Euclidiana(i, _currentInput.getDataSet());
			
			if(value < oldValue) {
				oldValue = value;
				index = i;
			}
			
		}
		
		return index;
	}
	
	/**
	 * Atualiza a taxa de aprendizado.
	 * 
	 * @param actualIteration taxa de aprendizado atual
	 * @param maxIteration iteração máxima do treinamento
	 */
	private void updateLearningRate(int actualIteration, int maxIteration){
		
		this._learnrate = this._firstLearnRate.multiply((BigDecimal.ONE.subtract((BigDecimal.valueOf(actualIteration).divide((BigDecimal.valueOf(maxIteration)), 15, RoundingMode.HALF_UP)))));
		
	}
 
	/**
	 * Inicializa o vetor de pesos de acordo com o input:
	 * 
	 * A - inicializa com zeros
	 * ALEATORIO - incializa com valores aleatórios entre 0 e 1
	 */
	private void initWeights(){
		
		if(_tipoInicializacaoPesos.toUpperCase().equals("A") || _tipoInicializacaoPesos.toUpperCase().equals("ALEATORIO")){
			for (int i = 0; i < _currentWeights.length; i++){
				for (int j = 0; j < _currentWeights[0].length; j++) {					
					_currentWeights[i][j] = _random.nextDouble();			
				}
			}			
		}else{			
			for (int i = 0; i < _currentWeights.length; i++){
				for (int j = 0; j < _currentWeights[0].length; j++) {					
					_currentWeights[i][j] = 0;			
				}
			}			
		}	
		
	}
 
	/**
	 * Imprime a matriz de pesos.
	 */
	public void printWeights()
	{
		for (int i = 0; i < _inputNeurons; i++)
		{
			for (int j = 0; j < _outputNeurons; j++)
			{
				System.out.print(_currentWeights[i][j] + "; ");
			}
			System.out.println();
		}
	}
 
	/**
	 * Imprime e gera um arquivo de report.
	 * @param count número da época
	 */
	public void printResults(int count) {
		
		File file;
		File report;
		FileWriter fw;
		BufferedWriter bw;
		FileWriter fwReport;
		BufferedWriter bwReport;
		StringBuilder sb;
		StringBuilder sbReport = new StringBuilder();
		int counterCorrectOnes = 0;
		int counterIncorrectOnes = 0;
		
		try {
							
			file = new File("result_"+count+".txt");
			report = new File("report_"+count+".txt");
			
			if (!file.exists()) {
				file.createNewFile();
			}
 
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			fwReport = new FileWriter(report.getAbsoluteFile());
			bwReport = new BufferedWriter(fwReport);
			
			Iterator<TrainingSet> it = _trainingSets.iterator();
			
			while(it.hasNext()){
				
				sb = new StringBuilder();
				
				TrainingSet trainingSet = it.next();
				
				setCurrentInput(trainingSet);
				
				for (int i = 0; i < _inputNeurons - 1; i++) {
					
					sb.append((trainingSet.getDataSet()[i]));
					
					if (i != _inputNeurons - 1) {
						sb.append(",");
					}
					
				}
				
				sb.append(" | ");
				sb.append(" Final => ");
				sb.append(trainingSet.getCurrentCategory());
				sb.append(" | ");
				sb.append(" Expected  => ");
				sb.append(trainingSet.getExpectedCategory());
				
				if(trainingSet.getCurrentCategory() == trainingSet.getExpectedCategory()) {
					counterCorrectOnes++;
				} else {
					counterIncorrectOnes++;
				}
				
				bw.write(sb.toString());
				
				bw.newLine();
			}
			
			sbReport.append("Corretas: ");
			sbReport.append(counterCorrectOnes);
			sbReport.append("\n");
			sbReport.append("Incorretas: ");
			sbReport.append(counterIncorrectOnes);
			sbReport.append("\n");
			sbReport.append("Porcentagem: ");
			sbReport.append(((counterCorrectOnes * 100) / (counterCorrectOnes+counterIncorrectOnes)));
			sbReport.append("%");
						
			bwReport.write(sbReport.toString());
			
			bw.close();
			bwReport.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
}