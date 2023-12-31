/*
 * Copyright (c) 2015, 2016, 2017 Adrian Siekierka
 *
 * This file is part of Charset.
 *
 * Charset is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Charset is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Charset.  If not, see <http://www.gnu.org/licenses/>.
 */

package betterwithmods.client.baking;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.EnumMap;

public abstract class BaseBakedModel implements IBakedModel {
    private final EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = new EnumMap<>(ItemCameraTransforms.TransformType.class);

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return ImmutablePair.of(this,
                transformMap.containsKey(cameraTransformType) ? transformMap.get(cameraTransformType).getMatrix() : null);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    public void addTransformation(ItemCameraTransforms.TransformType type, TRSRTransformation transformation) {
        transformMap.put(type, TRSRTransformation.blockCornerToCenter(transformation));
    }

    public void addThirdPersonTransformation(TRSRTransformation transformation) {
        addTransformation(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, transformation);
        addTransformation(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,  toLeftHand(transformation));
    }

    // ForgeBlockStateV1 transforms

    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    protected static TRSRTransformation toLeftHand(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }

    protected static TRSRTransformation getTransformation(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
                new Vector3f(s, s, s),
                null));
    }

    public BaseBakedModel addDefaultBlockTransforms() {
        //All transformations are forge default block transformations with y angle +180
        TRSRTransformation thirdperson = getTransformation(0, 2.5f, 0, 75, 225, 0, 0.375f);
        addTransformation(ItemCameraTransforms.TransformType.GUI, getTransformation(0, 0, 0, 30, 45, 0, 0.625f));
        addTransformation(ItemCameraTransforms.TransformType.GROUND, getTransformation(0, 0, 0, 0, 180, 0, 0.25f)); //Note: this is different from the default block transform oddly enough
        addTransformation(ItemCameraTransforms.TransformType.FIXED, getTransformation(0, 0, 0, 0, 180, 0, 0.5f));
        addThirdPersonTransformation(thirdperson);
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransformation(0, 0, 0, 0, 225, 0, 0.4f));
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, getTransformation(0, 0, 0, 0, 75, 0, 0.4f));
        return this;
    }
}
