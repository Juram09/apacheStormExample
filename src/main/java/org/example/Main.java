package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Crear una nueva topología
        TopologyBuilder builder = new TopologyBuilder();

        // Definir la fuente de datos (spout)
        builder.setSpout("sentence-spout", new RandomSentenceSpout(), 2);

        // Dividir la oración en palabras
        builder.setBolt("split-bolt", new SplitSentenceBolt(), 2)
                .shuffleGrouping("sentence-spout");

        // Contar las palabras
        builder.setBolt("count-bolt", new WordCountBolt(), 4)
                .fieldsGrouping("split-bolt", new Fields("word"));

        // Configuración
        Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(2);

        // Lanzar la topología
        if (args != null && args.length > 0) {
            // Argumento proporcionado, correr en un clúster real
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {
            // No hay argumentos, correr localmente
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test-topology", conf, builder.createTopology());
            Thread.sleep(10000); // Correr por 10 segundos
            cluster.shutdown();
        }
    }
}