/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sis.internal.netcdf;

import java.io.IOException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.test.DependsOn;
import org.apache.sis.test.DependsOnMethod;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.apache.sis.test.TestUtilities.getSingleton;


/**
 * Tests the {@link Grid} implementation. The default implementation tests
 * {@code org.apache.sis.internal.netcdf.ucar.GridGeometryWrapper} since the UCAR
 * library is our reference implementation. However subclasses can override the
 * {@link #createDecoder(TestData)} method in order to test a different implementation.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.0
 * @since   0.3
 * @module
 */
@DependsOn(VariableTest.class)
public strictfp class GridTest extends TestCase {
    /**
     * Optionally filters out some grid geometries that shall be ignored by the tests.
     * The default implementation returns the given array unmodified. This method is overridden by
     * {@code GridGeometryInfoTest} in order to ignore one-dimensional coordinate systems created
     * by {@code GridGeometry} but not by the UCAR library.
     *
     * @param  geometries  the grid geometries created by {@link Decoder}.
     * @return the grid geometries to test.
     */
    protected Grid[] filter(final Grid[] geometries) {
        return geometries;
    }

    /**
     * Tests {@link Grid#getSourceDimensions()} and {@link Grid#getTargetDimensions()}.
     *
     * @throws IOException if an I/O error occurred while opening the file.
     * @throws DataStoreException if a logical error occurred.
     */
    @Test
    public void testDimensions() throws IOException, DataStoreException {
        Grid geometry = getSingleton(filter(selectDataset(TestData.NETCDF_2D_GEOGRAPHIC).getGrids()));
        assertEquals("getSourceDimensions()", 2, geometry.getSourceDimensions());
        assertEquals("getTargetDimensions()", 2, geometry.getTargetDimensions());

        geometry = getSingleton(filter(selectDataset(TestData.NETCDF_4D_PROJECTED).getGrids()));
        assertEquals("getSourceDimensions()", 4, geometry.getSourceDimensions());
        assertEquals("getTargetDimensions()", 4, geometry.getTargetDimensions());
    }

    /**
     * Tests {@link Grid#getAxes(Decoder)} on a two-dimensional dataset.
     *
     * @throws IOException if an I/O error occurred while opening the file.
     * @throws DataStoreException if a logical error occurred.
     */
    @Test
    @DependsOnMethod("testDimensions")
    public void testAxes2D() throws IOException, DataStoreException {
        final Axis[] axes = getSingleton(filter(selectDataset(TestData.NETCDF_2D_GEOGRAPHIC).getGrids())).getAxes(decoder());
        assertEquals(2, axes.length);
        final Axis x = axes[0];
        final Axis y = axes[1];

        assertEquals('λ', x.abbreviation);
        assertEquals('φ', y.abbreviation);

        assertArrayEquals(new int[] {1}, x.sourceDimensions);
        assertArrayEquals(new int[] {0}, y.sourceDimensions);

        assertEquals(73, x.getSize());
        assertEquals(73, y.getSize());
    }

    /**
     * Tests {@link Grid#getAxes(Decoder)} on a four-dimensional dataset.
     *
     * @throws IOException if an I/O error occurred while opening the file.
     * @throws DataStoreException if a logical error occurred.
     */
    @Test
    @DependsOnMethod("testDimensions")
    public void testAxes4D() throws IOException, DataStoreException {
        final Axis[] axes = getSingleton(filter(selectDataset(TestData.NETCDF_4D_PROJECTED).getGrids())).getAxes(decoder());
        assertEquals(4, axes.length);
        final Axis x = axes[0];
        final Axis y = axes[1];
        final Axis z = axes[2];
        final Axis t = axes[3];

        assertEquals('x', x.abbreviation);
        assertEquals('y', y.abbreviation);
        assertEquals('H', z.abbreviation);
        assertEquals('t', t.abbreviation);

        assertArrayEquals(new int[] {3}, x.sourceDimensions);
        assertArrayEquals(new int[] {2}, y.sourceDimensions);
        assertArrayEquals(new int[] {1}, z.sourceDimensions);
        assertArrayEquals(new int[] {0}, t.sourceDimensions);

        assertEquals(38, x.getSize());
        assertEquals(19, y.getSize());
        assertEquals( 4, z.getSize());
        assertEquals( 1, t.getSize());
    }
}
