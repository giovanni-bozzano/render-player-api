// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package api.player.render.model;

import java.util.HashMap;
import java.util.Map;

public final class PlayerModelBaseSorting
{
    private String[] beforeLocalConstructingSuperiors;
    private String[] beforeLocalConstructingInferiors;
    private String[] afterLocalConstructingSuperiors;
    private String[] afterLocalConstructingInferiors;
    private Map<String, String[]> dynamicBeforeSuperiors;
    private Map<String, String[]> dynamicBeforeInferiors;
    private Map<String, String[]> dynamicOverrideSuperiors;
    private Map<String, String[]> dynamicOverrideInferiors;
    private Map<String, String[]> dynamicAfterSuperiors;
    private Map<String, String[]> dynamicAfterInferiors;
    // ############################################################################
    private String[] beforeAcceptSuperiors;
    private String[] beforeAcceptInferiors;
    private String[] overrideAcceptSuperiors;
    private String[] overrideAcceptInferiors;
    private String[] afterAcceptSuperiors;
    private String[] afterAcceptInferiors;
    // ############################################################################
    private String[] beforeGetArmForSideSuperiors;
    private String[] beforeGetArmForSideInferiors;
    private String[] overrideGetArmForSideSuperiors;
    private String[] overrideGetArmForSideInferiors;
    private String[] afterGetArmForSideSuperiors;
    private String[] afterGetArmForSideInferiors;
    // ############################################################################
    private String[] beforeGetMainHandSuperiors;
    private String[] beforeGetMainHandInferiors;
    private String[] overrideGetMainHandSuperiors;
    private String[] overrideGetMainHandInferiors;
    private String[] afterGetMainHandSuperiors;
    private String[] afterGetMainHandInferiors;
    // ############################################################################
    private String[] beforeGetRandomModelRendererSuperiors;
    private String[] beforeGetRandomModelRendererInferiors;
    private String[] overrideGetRandomModelRendererSuperiors;
    private String[] overrideGetRandomModelRendererInferiors;
    private String[] afterGetRandomModelRendererSuperiors;
    private String[] afterGetRandomModelRendererInferiors;
    // ############################################################################
    private String[] beforeRenderSuperiors;
    private String[] beforeRenderInferiors;
    private String[] overrideRenderSuperiors;
    private String[] overrideRenderInferiors;
    private String[] afterRenderSuperiors;
    private String[] afterRenderInferiors;
    // ############################################################################
    private String[] beforeRenderCapeSuperiors;
    private String[] beforeRenderCapeInferiors;
    private String[] overrideRenderCapeSuperiors;
    private String[] overrideRenderCapeInferiors;
    private String[] afterRenderCapeSuperiors;
    private String[] afterRenderCapeInferiors;
    // ############################################################################
    private String[] beforeRenderEarsSuperiors;
    private String[] beforeRenderEarsInferiors;
    private String[] overrideRenderEarsSuperiors;
    private String[] overrideRenderEarsInferiors;
    private String[] afterRenderEarsSuperiors;
    private String[] afterRenderEarsInferiors;
    // ############################################################################
    private String[] beforeSetLivingAnimationsSuperiors;
    private String[] beforeSetLivingAnimationsInferiors;
    private String[] overrideSetLivingAnimationsSuperiors;
    private String[] overrideSetLivingAnimationsInferiors;
    private String[] afterSetLivingAnimationsSuperiors;
    private String[] afterSetLivingAnimationsInferiors;
    // ############################################################################
    private String[] beforeSetModelAttributesSuperiors;
    private String[] beforeSetModelAttributesInferiors;
    private String[] overrideSetModelAttributesSuperiors;
    private String[] overrideSetModelAttributesInferiors;
    private String[] afterSetModelAttributesSuperiors;
    private String[] afterSetModelAttributesInferiors;
    // ############################################################################
    private String[] beforeSetRotationAnglesSuperiors;
    private String[] beforeSetRotationAnglesInferiors;
    private String[] overrideSetRotationAnglesSuperiors;
    private String[] overrideSetRotationAnglesInferiors;
    private String[] afterSetRotationAnglesSuperiors;
    private String[] afterSetRotationAnglesInferiors;
    // ############################################################################
    private String[] beforeSetVisibleSuperiors;
    private String[] beforeSetVisibleInferiors;
    private String[] overrideSetVisibleSuperiors;
    private String[] overrideSetVisibleInferiors;
    private String[] afterSetVisibleSuperiors;
    private String[] afterSetVisibleInferiors;
    // ############################################################################
    private String[] beforeTranslateHandSuperiors;
    private String[] beforeTranslateHandInferiors;
    private String[] overrideTranslateHandSuperiors;
    private String[] overrideTranslateHandInferiors;
    private String[] afterTranslateHandSuperiors;
    private String[] afterTranslateHandInferiors;

    // ############################################################################

    public String[] getBeforeLocalConstructingSuperiors()
    {
        return this.beforeLocalConstructingSuperiors;
    }

    public String[] getBeforeLocalConstructingInferiors()
    {
        return this.beforeLocalConstructingInferiors;
    }

    public String[] getAfterLocalConstructingSuperiors()
    {
        return this.afterLocalConstructingSuperiors;
    }

    public String[] getAfterLocalConstructingInferiors()
    {
        return this.afterLocalConstructingInferiors;
    }

    public void setBeforeLocalConstructingSuperiors(String[] value)
    {
        this.beforeLocalConstructingSuperiors = value;
    }

    public void setBeforeLocalConstructingInferiors(String[] value)
    {
        this.beforeLocalConstructingInferiors = value;
    }

    public void setAfterLocalConstructingSuperiors(String[] value)
    {
        this.afterLocalConstructingSuperiors = value;
    }

    public void setAfterLocalConstructingInferiors(String[] value)
    {
        this.afterLocalConstructingInferiors = value;
    }

    public Map<String, String[]> getDynamicBeforeSuperiors()
    {
        return this.dynamicBeforeSuperiors;
    }

    public Map<String, String[]> getDynamicBeforeInferiors()
    {
        return this.dynamicBeforeInferiors;
    }

    public Map<String, String[]> getDynamicOverrideSuperiors()
    {
        return this.dynamicOverrideSuperiors;
    }

    public Map<String, String[]> getDynamicOverrideInferiors()
    {
        return this.dynamicOverrideInferiors;
    }

    public Map<String, String[]> getDynamicAfterSuperiors()
    {
        return this.dynamicAfterSuperiors;
    }

    public Map<String, String[]> getDynamicAfterInferiors()
    {
        return this.dynamicAfterInferiors;
    }

    public void setDynamicBeforeSuperiors(String name, String[] superiors)
    {
        this.dynamicBeforeSuperiors = this.setDynamic(name, superiors, this.dynamicBeforeSuperiors);
    }

    public void setDynamicBeforeInferiors(String name, String[] inferiors)
    {
        this.dynamicBeforeInferiors = this.setDynamic(name, inferiors, this.dynamicBeforeInferiors);
    }

    public void setDynamicOverrideSuperiors(String name, String[] superiors)
    {
        this.dynamicOverrideSuperiors = this.setDynamic(name, superiors, this.dynamicOverrideSuperiors);
    }

    public void setDynamicOverrideInferiors(String name, String[] inferiors)
    {
        this.dynamicOverrideInferiors = this.setDynamic(name, inferiors, this.dynamicOverrideInferiors);
    }

    public void setDynamicAfterSuperiors(String name, String[] superiors)
    {
        this.dynamicAfterSuperiors = this.setDynamic(name, superiors, this.dynamicAfterSuperiors);
    }

    public void setDynamicAfterInferiors(String name, String[] inferiors)
    {
        this.dynamicAfterInferiors = this.setDynamic(name, inferiors, this.dynamicAfterInferiors);
    }

    private Map<String, String[]> setDynamic(String name, String[] names, Map<String, String[]> map)
    {
        if (name == null) {
            throw new IllegalArgumentException("Parameter 'name' may not be null");
        }

        if (names == null) {
            if (map != null) {
                map.remove(name);
            }
            return map;
        }

        if (map == null) {
            map = new HashMap<>();
        }
        map.put(name, names);

        return map;
    }

    // ############################################################################

    public String[] getBeforeAcceptSuperiors()
    {
        return this.beforeAcceptSuperiors;
    }

    public String[] getBeforeAcceptInferiors()
    {
        return this.beforeAcceptInferiors;
    }

    public String[] getOverrideAcceptSuperiors()
    {
        return this.overrideAcceptSuperiors;
    }

    public String[] getOverrideAcceptInferiors()
    {
        return this.overrideAcceptInferiors;
    }

    public String[] getAfterAcceptSuperiors()
    {
        return this.afterAcceptSuperiors;
    }

    public String[] getAfterAcceptInferiors()
    {
        return this.afterAcceptInferiors;
    }

    public void setBeforeAcceptSuperiors(String[] value)
    {
        this.beforeAcceptSuperiors = value;
    }

    public void setBeforeAcceptInferiors(String[] value)
    {
        this.beforeAcceptInferiors = value;
    }

    public void setOverrideAcceptSuperiors(String[] value)
    {
        this.overrideAcceptSuperiors = value;
    }

    public void setOverrideAcceptInferiors(String[] value)
    {
        this.overrideAcceptInferiors = value;
    }

    public void setAfterAcceptSuperiors(String[] value)
    {
        this.afterAcceptSuperiors = value;
    }

    public void setAfterAcceptInferiors(String[] value)
    {
        this.afterAcceptInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetArmForSideSuperiors()
    {
        return this.beforeGetArmForSideSuperiors;
    }

    public String[] getBeforeGetArmForSideInferiors()
    {
        return this.beforeGetArmForSideInferiors;
    }

    public String[] getOverrideGetArmForSideSuperiors()
    {
        return this.overrideGetArmForSideSuperiors;
    }

    public String[] getOverrideGetArmForSideInferiors()
    {
        return this.overrideGetArmForSideInferiors;
    }

    public String[] getAfterGetArmForSideSuperiors()
    {
        return this.afterGetArmForSideSuperiors;
    }

    public String[] getAfterGetArmForSideInferiors()
    {
        return this.afterGetArmForSideInferiors;
    }

    public void setBeforeGetArmForSideSuperiors(String[] value)
    {
        this.beforeGetArmForSideSuperiors = value;
    }

    public void setBeforeGetArmForSideInferiors(String[] value)
    {
        this.beforeGetArmForSideInferiors = value;
    }

    public void setOverrideGetArmForSideSuperiors(String[] value)
    {
        this.overrideGetArmForSideSuperiors = value;
    }

    public void setOverrideGetArmForSideInferiors(String[] value)
    {
        this.overrideGetArmForSideInferiors = value;
    }

    public void setAfterGetArmForSideSuperiors(String[] value)
    {
        this.afterGetArmForSideSuperiors = value;
    }

    public void setAfterGetArmForSideInferiors(String[] value)
    {
        this.afterGetArmForSideInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetMainHandSuperiors()
    {
        return this.beforeGetMainHandSuperiors;
    }

    public String[] getBeforeGetMainHandInferiors()
    {
        return this.beforeGetMainHandInferiors;
    }

    public String[] getOverrideGetMainHandSuperiors()
    {
        return this.overrideGetMainHandSuperiors;
    }

    public String[] getOverrideGetMainHandInferiors()
    {
        return this.overrideGetMainHandInferiors;
    }

    public String[] getAfterGetMainHandSuperiors()
    {
        return this.afterGetMainHandSuperiors;
    }

    public String[] getAfterGetMainHandInferiors()
    {
        return this.afterGetMainHandInferiors;
    }

    public void setBeforeGetMainHandSuperiors(String[] value)
    {
        this.beforeGetMainHandSuperiors = value;
    }

    public void setBeforeGetMainHandInferiors(String[] value)
    {
        this.beforeGetMainHandInferiors = value;
    }

    public void setOverrideGetMainHandSuperiors(String[] value)
    {
        this.overrideGetMainHandSuperiors = value;
    }

    public void setOverrideGetMainHandInferiors(String[] value)
    {
        this.overrideGetMainHandInferiors = value;
    }

    public void setAfterGetMainHandSuperiors(String[] value)
    {
        this.afterGetMainHandSuperiors = value;
    }

    public void setAfterGetMainHandInferiors(String[] value)
    {
        this.afterGetMainHandInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetRandomModelRendererSuperiors()
    {
        return this.beforeGetRandomModelRendererSuperiors;
    }

    public String[] getBeforeGetRandomModelRendererInferiors()
    {
        return this.beforeGetRandomModelRendererInferiors;
    }

    public String[] getOverrideGetRandomModelRendererSuperiors()
    {
        return this.overrideGetRandomModelRendererSuperiors;
    }

    public String[] getOverrideGetRandomModelRendererInferiors()
    {
        return this.overrideGetRandomModelRendererInferiors;
    }

    public String[] getAfterGetRandomModelRendererSuperiors()
    {
        return this.afterGetRandomModelRendererSuperiors;
    }

    public String[] getAfterGetRandomModelRendererInferiors()
    {
        return this.afterGetRandomModelRendererInferiors;
    }

    public void setBeforeGetRandomModelRendererSuperiors(String[] value)
    {
        this.beforeGetRandomModelRendererSuperiors = value;
    }

    public void setBeforeGetRandomModelRendererInferiors(String[] value)
    {
        this.beforeGetRandomModelRendererInferiors = value;
    }

    public void setOverrideGetRandomModelRendererSuperiors(String[] value)
    {
        this.overrideGetRandomModelRendererSuperiors = value;
    }

    public void setOverrideGetRandomModelRendererInferiors(String[] value)
    {
        this.overrideGetRandomModelRendererInferiors = value;
    }

    public void setAfterGetRandomModelRendererSuperiors(String[] value)
    {
        this.afterGetRandomModelRendererSuperiors = value;
    }

    public void setAfterGetRandomModelRendererInferiors(String[] value)
    {
        this.afterGetRandomModelRendererInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeRenderSuperiors()
    {
        return this.beforeRenderSuperiors;
    }

    public String[] getBeforeRenderInferiors()
    {
        return this.beforeRenderInferiors;
    }

    public String[] getOverrideRenderSuperiors()
    {
        return this.overrideRenderSuperiors;
    }

    public String[] getOverrideRenderInferiors()
    {
        return this.overrideRenderInferiors;
    }

    public String[] getAfterRenderSuperiors()
    {
        return this.afterRenderSuperiors;
    }

    public String[] getAfterRenderInferiors()
    {
        return this.afterRenderInferiors;
    }

    public void setBeforeRenderSuperiors(String[] value)
    {
        this.beforeRenderSuperiors = value;
    }

    public void setBeforeRenderInferiors(String[] value)
    {
        this.beforeRenderInferiors = value;
    }

    public void setOverrideRenderSuperiors(String[] value)
    {
        this.overrideRenderSuperiors = value;
    }

    public void setOverrideRenderInferiors(String[] value)
    {
        this.overrideRenderInferiors = value;
    }

    public void setAfterRenderSuperiors(String[] value)
    {
        this.afterRenderSuperiors = value;
    }

    public void setAfterRenderInferiors(String[] value)
    {
        this.afterRenderInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeRenderCapeSuperiors()
    {
        return this.beforeRenderCapeSuperiors;
    }

    public String[] getBeforeRenderCapeInferiors()
    {
        return this.beforeRenderCapeInferiors;
    }

    public String[] getOverrideRenderCapeSuperiors()
    {
        return this.overrideRenderCapeSuperiors;
    }

    public String[] getOverrideRenderCapeInferiors()
    {
        return this.overrideRenderCapeInferiors;
    }

    public String[] getAfterRenderCapeSuperiors()
    {
        return this.afterRenderCapeSuperiors;
    }

    public String[] getAfterRenderCapeInferiors()
    {
        return this.afterRenderCapeInferiors;
    }

    public void setBeforeRenderCapeSuperiors(String[] value)
    {
        this.beforeRenderCapeSuperiors = value;
    }

    public void setBeforeRenderCapeInferiors(String[] value)
    {
        this.beforeRenderCapeInferiors = value;
    }

    public void setOverrideRenderCapeSuperiors(String[] value)
    {
        this.overrideRenderCapeSuperiors = value;
    }

    public void setOverrideRenderCapeInferiors(String[] value)
    {
        this.overrideRenderCapeInferiors = value;
    }

    public void setAfterRenderCapeSuperiors(String[] value)
    {
        this.afterRenderCapeSuperiors = value;
    }

    public void setAfterRenderCapeInferiors(String[] value)
    {
        this.afterRenderCapeInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeRenderEarsSuperiors()
    {
        return this.beforeRenderEarsSuperiors;
    }

    public String[] getBeforeRenderEarsInferiors()
    {
        return this.beforeRenderEarsInferiors;
    }

    public String[] getOverrideRenderEarsSuperiors()
    {
        return this.overrideRenderEarsSuperiors;
    }

    public String[] getOverrideRenderEarsInferiors()
    {
        return this.overrideRenderEarsInferiors;
    }

    public String[] getAfterRenderEarsSuperiors()
    {
        return this.afterRenderEarsSuperiors;
    }

    public String[] getAfterRenderEarsInferiors()
    {
        return this.afterRenderEarsInferiors;
    }

    public void setBeforeRenderEarsSuperiors(String[] value)
    {
        this.beforeRenderEarsSuperiors = value;
    }

    public void setBeforeRenderEarsInferiors(String[] value)
    {
        this.beforeRenderEarsInferiors = value;
    }

    public void setOverrideRenderEarsSuperiors(String[] value)
    {
        this.overrideRenderEarsSuperiors = value;
    }

    public void setOverrideRenderEarsInferiors(String[] value)
    {
        this.overrideRenderEarsInferiors = value;
    }

    public void setAfterRenderEarsSuperiors(String[] value)
    {
        this.afterRenderEarsSuperiors = value;
    }

    public void setAfterRenderEarsInferiors(String[] value)
    {
        this.afterRenderEarsInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeSetLivingAnimationsSuperiors()
    {
        return this.beforeSetLivingAnimationsSuperiors;
    }

    public String[] getBeforeSetLivingAnimationsInferiors()
    {
        return this.beforeSetLivingAnimationsInferiors;
    }

    public String[] getOverrideSetLivingAnimationsSuperiors()
    {
        return this.overrideSetLivingAnimationsSuperiors;
    }

    public String[] getOverrideSetLivingAnimationsInferiors()
    {
        return this.overrideSetLivingAnimationsInferiors;
    }

    public String[] getAfterSetLivingAnimationsSuperiors()
    {
        return this.afterSetLivingAnimationsSuperiors;
    }

    public String[] getAfterSetLivingAnimationsInferiors()
    {
        return this.afterSetLivingAnimationsInferiors;
    }

    public void setBeforeSetLivingAnimationsSuperiors(String[] value)
    {
        this.beforeSetLivingAnimationsSuperiors = value;
    }

    public void setBeforeSetLivingAnimationsInferiors(String[] value)
    {
        this.beforeSetLivingAnimationsInferiors = value;
    }

    public void setOverrideSetLivingAnimationsSuperiors(String[] value)
    {
        this.overrideSetLivingAnimationsSuperiors = value;
    }

    public void setOverrideSetLivingAnimationsInferiors(String[] value)
    {
        this.overrideSetLivingAnimationsInferiors = value;
    }

    public void setAfterSetLivingAnimationsSuperiors(String[] value)
    {
        this.afterSetLivingAnimationsSuperiors = value;
    }

    public void setAfterSetLivingAnimationsInferiors(String[] value)
    {
        this.afterSetLivingAnimationsInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeSetModelAttributesSuperiors()
    {
        return this.beforeSetModelAttributesSuperiors;
    }

    public String[] getBeforeSetModelAttributesInferiors()
    {
        return this.beforeSetModelAttributesInferiors;
    }

    public String[] getOverrideSetModelAttributesSuperiors()
    {
        return this.overrideSetModelAttributesSuperiors;
    }

    public String[] getOverrideSetModelAttributesInferiors()
    {
        return this.overrideSetModelAttributesInferiors;
    }

    public String[] getAfterSetModelAttributesSuperiors()
    {
        return this.afterSetModelAttributesSuperiors;
    }

    public String[] getAfterSetModelAttributesInferiors()
    {
        return this.afterSetModelAttributesInferiors;
    }

    public void setBeforeSetModelAttributesSuperiors(String[] value)
    {
        this.beforeSetModelAttributesSuperiors = value;
    }

    public void setBeforeSetModelAttributesInferiors(String[] value)
    {
        this.beforeSetModelAttributesInferiors = value;
    }

    public void setOverrideSetModelAttributesSuperiors(String[] value)
    {
        this.overrideSetModelAttributesSuperiors = value;
    }

    public void setOverrideSetModelAttributesInferiors(String[] value)
    {
        this.overrideSetModelAttributesInferiors = value;
    }

    public void setAfterSetModelAttributesSuperiors(String[] value)
    {
        this.afterSetModelAttributesSuperiors = value;
    }

    public void setAfterSetModelAttributesInferiors(String[] value)
    {
        this.afterSetModelAttributesInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeSetRotationAnglesSuperiors()
    {
        return this.beforeSetRotationAnglesSuperiors;
    }

    public String[] getBeforeSetRotationAnglesInferiors()
    {
        return this.beforeSetRotationAnglesInferiors;
    }

    public String[] getOverrideSetRotationAnglesSuperiors()
    {
        return this.overrideSetRotationAnglesSuperiors;
    }

    public String[] getOverrideSetRotationAnglesInferiors()
    {
        return this.overrideSetRotationAnglesInferiors;
    }

    public String[] getAfterSetRotationAnglesSuperiors()
    {
        return this.afterSetRotationAnglesSuperiors;
    }

    public String[] getAfterSetRotationAnglesInferiors()
    {
        return this.afterSetRotationAnglesInferiors;
    }

    public void setBeforeSetRotationAnglesSuperiors(String[] value)
    {
        this.beforeSetRotationAnglesSuperiors = value;
    }

    public void setBeforeSetRotationAnglesInferiors(String[] value)
    {
        this.beforeSetRotationAnglesInferiors = value;
    }

    public void setOverrideSetRotationAnglesSuperiors(String[] value)
    {
        this.overrideSetRotationAnglesSuperiors = value;
    }

    public void setOverrideSetRotationAnglesInferiors(String[] value)
    {
        this.overrideSetRotationAnglesInferiors = value;
    }

    public void setAfterSetRotationAnglesSuperiors(String[] value)
    {
        this.afterSetRotationAnglesSuperiors = value;
    }

    public void setAfterSetRotationAnglesInferiors(String[] value)
    {
        this.afterSetRotationAnglesInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeSetVisibleSuperiors()
    {
        return this.beforeSetVisibleSuperiors;
    }

    public String[] getBeforeSetVisibleInferiors()
    {
        return this.beforeSetVisibleInferiors;
    }

    public String[] getOverrideSetVisibleSuperiors()
    {
        return this.overrideSetVisibleSuperiors;
    }

    public String[] getOverrideSetVisibleInferiors()
    {
        return this.overrideSetVisibleInferiors;
    }

    public String[] getAfterSetVisibleSuperiors()
    {
        return this.afterSetVisibleSuperiors;
    }

    public String[] getAfterSetVisibleInferiors()
    {
        return this.afterSetVisibleInferiors;
    }

    public void setBeforeSetVisibleSuperiors(String[] value)
    {
        this.beforeSetVisibleSuperiors = value;
    }

    public void setBeforeSetVisibleInferiors(String[] value)
    {
        this.beforeSetVisibleInferiors = value;
    }

    public void setOverrideSetVisibleSuperiors(String[] value)
    {
        this.overrideSetVisibleSuperiors = value;
    }

    public void setOverrideSetVisibleInferiors(String[] value)
    {
        this.overrideSetVisibleInferiors = value;
    }

    public void setAfterSetVisibleSuperiors(String[] value)
    {
        this.afterSetVisibleSuperiors = value;
    }

    public void setAfterSetVisibleInferiors(String[] value)
    {
        this.afterSetVisibleInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeTranslateHandSuperiors()
    {
        return this.beforeTranslateHandSuperiors;
    }

    public String[] getBeforeTranslateHandInferiors()
    {
        return this.beforeTranslateHandInferiors;
    }

    public String[] getOverrideTranslateHandSuperiors()
    {
        return this.overrideTranslateHandSuperiors;
    }

    public String[] getOverrideTranslateHandInferiors()
    {
        return this.overrideTranslateHandInferiors;
    }

    public String[] getAfterTranslateHandSuperiors()
    {
        return this.afterTranslateHandSuperiors;
    }

    public String[] getAfterTranslateHandInferiors()
    {
        return this.afterTranslateHandInferiors;
    }

    public void setBeforeTranslateHandSuperiors(String[] value)
    {
        this.beforeTranslateHandSuperiors = value;
    }

    public void setBeforeTranslateHandInferiors(String[] value)
    {
        this.beforeTranslateHandInferiors = value;
    }

    public void setOverrideTranslateHandSuperiors(String[] value)
    {
        this.overrideTranslateHandSuperiors = value;
    }

    public void setOverrideTranslateHandInferiors(String[] value)
    {
        this.overrideTranslateHandInferiors = value;
    }

    public void setAfterTranslateHandSuperiors(String[] value)
    {
        this.afterTranslateHandSuperiors = value;
    }

    public void setAfterTranslateHandInferiors(String[] value)
    {
        this.afterTranslateHandInferiors = value;
    }
}
