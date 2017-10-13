package com.hs.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.wifitz.utils.HdfsUtil;

class wordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		Text word = new Text();
		String[] lines = value.toString().split(",");

		for (String w : lines) {
			word.set(w);
			context.write(word, new IntWritable(1));
		}
		
	}
}

class wordReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int sum =0;
		for (IntWritable value : values) {
			sum += value.get();
		}
		
		context.write(key, new IntWritable(sum));
	}
}

class wordPartitioner extends Partitioner<Text, IntWritable>{

	@Override
	public int getPartition(Text key, IntWritable values, int numPartitions) {
		String keys = key.toString();
		if (keys.length()<4) {
			return 0%numPartitions;
		} else if (keys.length()>=4 && keys.length()<6) {
			return 1%numPartitions;
		} else {
			return 2%numPartitions;
		}	
	}	
}

class HashPartitioner extends Partitioner<Text, Text>
{
    public int getPartition(Text key, Text value, int numReduceTasks)
    {
       //System.out.println("num tasks: " + numReduceTasks + "key:" + key);
       //System.out.println("key："+key.toString()+"   return :"+((key.toString().hashCode() & Integer.MAX_VALUE) % numReduceTasks));
        return (key.toString().hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    }
}

//class wordCombiner extends CombinerRunner<K, V>

public class WordCount {
	public static String outputFilePath = "/output/first";
	
	public static String inputFilePath = "/input/s.txt";
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		Long starttimer  = System.currentTimeMillis();
		//String inputFilePath = "/keliuliang/macdata/chen/day/2017/*";

		System.out.println(" WordCount test");
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "wc");
		job.setJarByClass(WordCount.class);
		
		job.setMapperClass(wordMapper.class);
		job.setReducerClass(wordReduce.class);
		//job.setCombinerClass(wordReduce.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		// 注锟斤拷 锟斤拷锟斤拷锟剿凤拷锟斤拷之锟斤拷 要锟斤拷锟斤拷reduce锟侥革拷锟斤拷
		//job.setPartitionerClass(HashPartitioner.class);
		//job.setNumReduceTasks(34);
		
		Path outputPath = new Path(outputFilePath);
		outputPath.getFileSystem(conf).delete(outputPath, true);
		
		FileInputFormat.addInputPath(job, new Path(inputFilePath));
		FileOutputFormat.setOutputPath(job, new Path(outputFilePath));
		
		if (job.waitForCompletion(true)) {
			System.out.println("");
			Long end  = System.currentTimeMillis();
			System.out.println("use time:"+(end-starttimer));
		}
	}
	
}










