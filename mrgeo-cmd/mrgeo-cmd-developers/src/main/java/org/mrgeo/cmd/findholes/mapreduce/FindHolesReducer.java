package org.mrgeo.cmd.findholes.mapreduce;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.mrgeo.data.DataProviderFactory;
import org.mrgeo.data.DataProviderFactory.AccessMode;
import org.mrgeo.data.adhoc.AdHocDataProvider;
import org.mrgeo.data.tile.TileIdWritable;
import org.mrgeo.image.ImageStats;
import org.mrgeo.utils.LongRectangle;


public class FindHolesReducer extends Reducer<LongWritable, LongWritable, Text, Text>{

	private LongRectangle lr = null;
	private long minX, minY, maxX, maxY;
	private long width;
	//private String adhoc;
	
	private OutputStream os = null;
	private PrintWriter pw = null;
	
	public void setup(Context context) throws IOException, InterruptedException{
		// get zoom level and bounds(tile space)
		Configuration conf = context.getConfiguration();
		String bounds = conf.get("bounds");
		String[] vals = bounds.split(",");
		if(vals.length == 4){
			minX = Long.parseLong(vals[0]);
			minY = Long.parseLong(vals[1]);
			maxX = Long.parseLong(vals[2]);
			maxY = Long.parseLong(vals[3]);
		}
		
		width = maxX - minX + 1;

		String adhoc = conf.get("adhoc.provider");
		AdHocDataProvider provider = DataProviderFactory.getAdHocDataProvider(adhoc,
		        AccessMode.WRITE, context.getConfiguration());
		os = provider.add();
		pw = new PrintWriter(os);
		
		
	} // end setup
	
	public void cleanup(Context context) throws IOException, InterruptedException{
		
		pw.close();
		os.close();
		
	} // cleanup
	
	
	public void reduce(LongWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException{
		
		
		
		int[] valid = new int[(int)width];
		for(LongWritable v : values){
			int indx = (int)(v.get() - minX);
			valid[indx]++;
		}
		pw.print(key.toString() + ": ");
		for(int x = 0; x < width; x++){

			if(valid[x] > 0){
				pw.print(Long.toString(x + minX) + " ");
				//context.write(new Text(key.toString()), new Text(Long.toString(x + minX)));
			}
		}
		pw.println();
		
	} // end reduce

	
} // end FindHolesReducer