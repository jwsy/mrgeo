/*
 * Copyright (c) 2009-2010 by SPADAC Inc.  All rights reserved.
 */

package org.mrgeo.paint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Composite (merge) operation for two rasters.  Treats each raster value as a double.
 */
public class AdditiveCompositeDouble extends WeightedComposite
{
  @SuppressWarnings("unused")
  private static final Logger log = LoggerFactory.getLogger(AdditiveCompositeDouble.class);

  public AdditiveCompositeDouble()
  {
    super();
  }
  
  public AdditiveCompositeDouble(final double weight)
  {
    super(weight);
  }
  
  public AdditiveCompositeDouble(double weight, double nodata)
  {
    super(weight, nodata);
  }

  
  private class AdditiveCompositeDoubleContext implements CompositeContext
  {
    public AdditiveCompositeDoubleContext()
    {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.CompositeContext#compose(java.awt.image.Raster,
     * java.awt.image.Raster, java.awt.image.WritableRaster)
     */
    @Override
    public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
    {
      int minX = dstOut.getMinX();
      int minY = dstOut.getMinY();
      int maxX = minX + dstOut.getWidth();
      int maxY = minY + dstOut.getHeight();

      //log.debug("minX,minY,maxX,maxY: " + minX + "," + minY + "," + maxX + "," + maxY);
      for (int y = minY; y < maxY; y++)
      {
        for (int x = minX; x < maxX; x++)
        {
          double d = dstIn.getSampleDouble(x, y, 0);
          if (isNodataNaN)
          {
            if (Double.isNaN(d))
            {
              d = 0;
            }
          }
          else
          {
            if (d == nodata)
            {
              d = 0;
            }
          }
          double sample = (src.getSampleDouble(x, y, 0) * weight) + d;

          dstOut.setSample(x, y, 0, sample);
        }
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.CompositeContext#dispose()
     */
    @Override
    public void dispose()
    {

    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Composite#createContext(java.awt.image.ColorModel,
   * java.awt.image.ColorModel, java.awt.RenderingHints)
   */
  @Override
  public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel,
      RenderingHints hints)
  {
    return new AdditiveCompositeDoubleContext();
  }
}