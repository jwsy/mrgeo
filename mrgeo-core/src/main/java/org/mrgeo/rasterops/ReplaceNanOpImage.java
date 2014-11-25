/*
 * Copyright 2009-2014 DigitalGlobe, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mrgeo.rasterops;

import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Vector;

@SuppressWarnings("unchecked")
public final class ReplaceNanOpImage extends OpImage
{
  double newValue;
  
  /**
   * Sets any value less than min or greater than max to NaN. If either min or max are NaN they 
   * are ignored.
   * @param src
   * @param min
   * @param max
   * @return
   * @throws IOException
   */
  public static ReplaceNanOpImage create(RenderedImage src, double newValue)
      throws IOException
  {
    @SuppressWarnings("rawtypes")
    Vector sources = new Vector();
    sources.add(src);
    return new ReplaceNanOpImage(sources, newValue);
  }

  @SuppressWarnings("rawtypes")
  private ReplaceNanOpImage(Vector sources, double newValue)
  {
    super(sources, null, null, false);

    this.newValue = newValue;
    RenderedImage src = (RenderedImage) sources.get(0);
    
    colorModel = src.getColorModel();
    sampleModel = src.getSampleModel();
  }

  @Override
  protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect)
  {
    final Raster r1 = sources[0].getData(destRect);
    int test = 0;
    for (int y = destRect.y; y < destRect.y + destRect.height; y++)
    {
      for (int x = destRect.x; x < destRect.x + destRect.width; x++)
      {
        for (int band = 0; band < r1.getNumBands(); band++)
        {
          double v = r1.getSampleDouble(x, y, band);
          if (Double.isNaN(v))
          {
            v = newValue;
          }
          dest.setSample(x, y, band, v);
        }
      }
    }
  }

  @Override
  public Rectangle mapDestRect(Rectangle destRect, int sourceIndex)
  {
    return destRect;
  }

  @Override
  public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex)
  {
    return sourceRect;
  }
  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }

}