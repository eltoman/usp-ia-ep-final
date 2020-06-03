README

Instruções para a execução do programa
O EP foi desenvolvido em linguagem de programação Java, para executa-lo é necessário ter o ambiente padrão de desenvolvimento java configurado em sua máquina (virtual machine e JDK). 

Copiar a pasta "TrabalhoIA" inteira para o diretório de sua escolha;
No seu diretório (fora da pasta EP, dentro da pasta TrabalhoIA) rodar:

javac EP\Main.java
java EP.Main {informar os parametros}

Parametros de Execução:

ArquivoTreino: Path completo
ArquivoValidacao: Path completo
ArquivoTeste: Path completo
TaxaAprendizado: taxa de aprendizado desejada
NeuroniosCamadaEscondida: Quantidade desejada  
NeuroniosClasse: Quantidade desejada  
TipoInicializacaoPeso: Informar "A" (para aleatório) ou 0 (para 0) 

Chamada:
java EP.Main ArquivoTreino ArquivoValidacao ArquivoTeste TaxaAprendizado NeuroniosCamadaEscondida NeuroniosClasse TipoInicializacaoPeso

Exemplo de Chamada
java EP.Main optdigits.tra optdigits.val optdigits.tes 0.01 10 10  A

Arquivos de Saída

O EP irá gerar os sequintes arquivos de sáida:

resultadosMLP.txt - Resultado (Classes obtidas) do conjunto de treinamento
avaliacaoMLP.txt - Mediadas de avaliação do algoritmo
pesosMLP.txt - REsultado dos pesos finais

resultadosLVQ.txt - Resultado (Classes obtidas) do conjunto de treinamento 
avaliacaoLVQ.txt - Mediadas de avaliação do algoritmo
pesosLVQ.txt - REsultado dos pesos finais


