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
package api.player.render.renderer;

import java.util.HashMap;
import java.util.Map;

public final class PlayerRendererBaseSorting
{
    private String[] beforeLocalConstructingSuperiors = null;
    private String[] beforeLocalConstructingInferiors = null;
    private String[] afterLocalConstructingSuperiors = null;
    private String[] afterLocalConstructingInferiors = null;
    private Map<String, String[]> dynamicBeforeSuperiors = null;
    private Map<String, String[]> dynamicBeforeInferiors = null;
    private Map<String, String[]> dynamicOverrideSuperiors = null;
    private Map<String, String[]> dynamicOverrideInferiors = null;
    private Map<String, String[]> dynamicAfterSuperiors = null;
    private Map<String, String[]> dynamicAfterInferiors = null;
    // ############################################################################
    private String[] beforeApplyRotationsSuperiors = null;
    private String[] beforeApplyRotationsInferiors = null;
    private String[] overrideApplyRotationsSuperiors = null;
    private String[] overrideApplyRotationsInferiors = null;
    private String[] afterApplyRotationsSuperiors = null;
    private String[] afterApplyRotationsInferiors = null;
    // ############################################################################
    private String[] beforeCanRenderNameSuperiors = null;
    private String[] beforeCanRenderNameInferiors = null;
    private String[] overrideCanRenderNameSuperiors = null;
    private String[] overrideCanRenderNameInferiors = null;
    private String[] afterCanRenderNameSuperiors = null;
    private String[] afterCanRenderNameInferiors = null;
    // ############################################################################
    private String[] beforeGetArmPoseSuperiors = null;
    private String[] beforeGetArmPoseInferiors = null;
    private String[] overrideGetArmPoseSuperiors = null;
    private String[] overrideGetArmPoseInferiors = null;
    private String[] afterGetArmPoseSuperiors = null;
    private String[] afterGetArmPoseInferiors = null;
    // ############################################################################
    private String[] beforeGetDeathMaxRotationSuperiors = null;
    private String[] beforeGetDeathMaxRotationInferiors = null;
    private String[] overrideGetDeathMaxRotationSuperiors = null;
    private String[] overrideGetDeathMaxRotationInferiors = null;
    private String[] afterGetDeathMaxRotationSuperiors = null;
    private String[] afterGetDeathMaxRotationInferiors = null;
    // ############################################################################
    private String[] beforeGetEntityModelSuperiors = null;
    private String[] beforeGetEntityModelInferiors = null;
    private String[] overrideGetEntityModelSuperiors = null;
    private String[] overrideGetEntityModelInferiors = null;
    private String[] afterGetEntityModelSuperiors = null;
    private String[] afterGetEntityModelInferiors = null;
    // ############################################################################
    private String[] beforeGetEntityTextureSuperiors = null;
    private String[] beforeGetEntityTextureInferiors = null;
    private String[] overrideGetEntityTextureSuperiors = null;
    private String[] overrideGetEntityTextureInferiors = null;
    private String[] afterGetEntityTextureSuperiors = null;
    private String[] afterGetEntityTextureInferiors = null;
    // ############################################################################
    private String[] beforeGetFontRendererFromRenderManagerSuperiors = null;
    private String[] beforeGetFontRendererFromRenderManagerInferiors = null;
    private String[] overrideGetFontRendererFromRenderManagerSuperiors = null;
    private String[] overrideGetFontRendererFromRenderManagerInferiors = null;
    private String[] afterGetFontRendererFromRenderManagerSuperiors = null;
    private String[] afterGetFontRendererFromRenderManagerInferiors = null;
    // ############################################################################
    private String[] beforeGetRenderManagerSuperiors = null;
    private String[] beforeGetRenderManagerInferiors = null;
    private String[] overrideGetRenderManagerSuperiors = null;
    private String[] overrideGetRenderManagerInferiors = null;
    private String[] afterGetRenderManagerSuperiors = null;
    private String[] afterGetRenderManagerInferiors = null;
    // ############################################################################
    private String[] beforeGetRenderOffsetSuperiors = null;
    private String[] beforeGetRenderOffsetInferiors = null;
    private String[] overrideGetRenderOffsetSuperiors = null;
    private String[] overrideGetRenderOffsetInferiors = null;
    private String[] afterGetRenderOffsetSuperiors = null;
    private String[] afterGetRenderOffsetInferiors = null;
    // ############################################################################
    private String[] beforeGetSwingProgressSuperiors = null;
    private String[] beforeGetSwingProgressInferiors = null;
    private String[] overrideGetSwingProgressSuperiors = null;
    private String[] overrideGetSwingProgressInferiors = null;
    private String[] afterGetSwingProgressSuperiors = null;
    private String[] afterGetSwingProgressInferiors = null;
    // ############################################################################
    private String[] beforeHandleRotationFloatSuperiors = null;
    private String[] beforeHandleRotationFloatInferiors = null;
    private String[] overrideHandleRotationFloatSuperiors = null;
    private String[] overrideHandleRotationFloatInferiors = null;
    private String[] afterHandleRotationFloatSuperiors = null;
    private String[] afterHandleRotationFloatInferiors = null;
    // ############################################################################
    private String[] beforePreRenderCallbackSuperiors = null;
    private String[] beforePreRenderCallbackInferiors = null;
    private String[] overridePreRenderCallbackSuperiors = null;
    private String[] overridePreRenderCallbackInferiors = null;
    private String[] afterPreRenderCallbackSuperiors = null;
    private String[] afterPreRenderCallbackInferiors = null;
    // ############################################################################
    private String[] beforeRenderSuperiors = null;
    private String[] beforeRenderInferiors = null;
    private String[] overrideRenderSuperiors = null;
    private String[] overrideRenderInferiors = null;
    private String[] afterRenderSuperiors = null;
    private String[] afterRenderInferiors = null;
    // ############################################################################
    private String[] beforeRenderItemSuperiors = null;
    private String[] beforeRenderItemInferiors = null;
    private String[] overrideRenderItemSuperiors = null;
    private String[] overrideRenderItemInferiors = null;
    private String[] afterRenderItemSuperiors = null;
    private String[] afterRenderItemInferiors = null;
    // ############################################################################
    private String[] beforeRenderLeftArmSuperiors = null;
    private String[] beforeRenderLeftArmInferiors = null;
    private String[] overrideRenderLeftArmSuperiors = null;
    private String[] overrideRenderLeftArmInferiors = null;
    private String[] afterRenderLeftArmSuperiors = null;
    private String[] afterRenderLeftArmInferiors = null;
    // ############################################################################
    private String[] beforeRenderNameSuperiors = null;
    private String[] beforeRenderNameInferiors = null;
    private String[] overrideRenderNameSuperiors = null;
    private String[] overrideRenderNameInferiors = null;
    private String[] afterRenderNameSuperiors = null;
    private String[] afterRenderNameInferiors = null;
    // ############################################################################
    private String[] beforeRenderRightArmSuperiors = null;
    private String[] beforeRenderRightArmInferiors = null;
    private String[] overrideRenderRightArmSuperiors = null;
    private String[] overrideRenderRightArmInferiors = null;
    private String[] afterRenderRightArmSuperiors = null;
    private String[] afterRenderRightArmInferiors = null;
    // ############################################################################
    private String[] beforeSetModelVisibilitiesSuperiors = null;
    private String[] beforeSetModelVisibilitiesInferiors = null;
    private String[] overrideSetModelVisibilitiesSuperiors = null;
    private String[] overrideSetModelVisibilitiesInferiors = null;
    private String[] afterSetModelVisibilitiesSuperiors = null;
    private String[] afterSetModelVisibilitiesInferiors = null;
    // ############################################################################
    private String[] beforeShouldRenderSuperiors = null;
    private String[] beforeShouldRenderInferiors = null;
    private String[] overrideShouldRenderSuperiors = null;
    private String[] overrideShouldRenderInferiors = null;
    private String[] afterShouldRenderSuperiors = null;
    private String[] afterShouldRenderInferiors = null;

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

    public String[] getBeforeApplyRotationsSuperiors()
    {
        return this.beforeApplyRotationsSuperiors;
    }

    public String[] getBeforeApplyRotationsInferiors()
    {
        return this.beforeApplyRotationsInferiors;
    }

    public String[] getOverrideApplyRotationsSuperiors()
    {
        return this.overrideApplyRotationsSuperiors;
    }

    public String[] getOverrideApplyRotationsInferiors()
    {
        return this.overrideApplyRotationsInferiors;
    }

    public String[] getAfterApplyRotationsSuperiors()
    {
        return this.afterApplyRotationsSuperiors;
    }

    public String[] getAfterApplyRotationsInferiors()
    {
        return this.afterApplyRotationsInferiors;
    }

    public void setBeforeApplyRotationsSuperiors(String[] value)
    {
        this.beforeApplyRotationsSuperiors = value;
    }

    public void setBeforeApplyRotationsInferiors(String[] value)
    {
        this.beforeApplyRotationsInferiors = value;
    }

    public void setOverrideApplyRotationsSuperiors(String[] value)
    {
        this.overrideApplyRotationsSuperiors = value;
    }

    public void setOverrideApplyRotationsInferiors(String[] value)
    {
        this.overrideApplyRotationsInferiors = value;
    }

    public void setAfterApplyRotationsSuperiors(String[] value)
    {
        this.afterApplyRotationsSuperiors = value;
    }

    public void setAfterApplyRotationsInferiors(String[] value)
    {
        this.afterApplyRotationsInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeCanRenderNameSuperiors()
    {
        return this.beforeCanRenderNameSuperiors;
    }

    public String[] getBeforeCanRenderNameInferiors()
    {
        return this.beforeCanRenderNameInferiors;
    }

    public String[] getOverrideCanRenderNameSuperiors()
    {
        return this.overrideCanRenderNameSuperiors;
    }

    public String[] getOverrideCanRenderNameInferiors()
    {
        return this.overrideCanRenderNameInferiors;
    }

    public String[] getAfterCanRenderNameSuperiors()
    {
        return this.afterCanRenderNameSuperiors;
    }

    public String[] getAfterCanRenderNameInferiors()
    {
        return this.afterCanRenderNameInferiors;
    }

    public void setBeforeCanRenderNameSuperiors(String[] value)
    {
        this.beforeCanRenderNameSuperiors = value;
    }

    public void setBeforeCanRenderNameInferiors(String[] value)
    {
        this.beforeCanRenderNameInferiors = value;
    }

    public void setOverrideCanRenderNameSuperiors(String[] value)
    {
        this.overrideCanRenderNameSuperiors = value;
    }

    public void setOverrideCanRenderNameInferiors(String[] value)
    {
        this.overrideCanRenderNameInferiors = value;
    }

    public void setAfterCanRenderNameSuperiors(String[] value)
    {
        this.afterCanRenderNameSuperiors = value;
    }

    public void setAfterCanRenderNameInferiors(String[] value)
    {
        this.afterCanRenderNameInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetArmPoseSuperiors()
    {
        return this.beforeGetArmPoseSuperiors;
    }

    public String[] getBeforeGetArmPoseInferiors()
    {
        return this.beforeGetArmPoseInferiors;
    }

    public String[] getOverrideGetArmPoseSuperiors()
    {
        return this.overrideGetArmPoseSuperiors;
    }

    public String[] getOverrideGetArmPoseInferiors()
    {
        return this.overrideGetArmPoseInferiors;
    }

    public String[] getAfterGetArmPoseSuperiors()
    {
        return this.afterGetArmPoseSuperiors;
    }

    public String[] getAfterGetArmPoseInferiors()
    {
        return this.afterGetArmPoseInferiors;
    }

    public void setBeforeGetArmPoseSuperiors(String[] value)
    {
        this.beforeGetArmPoseSuperiors = value;
    }

    public void setBeforeGetArmPoseInferiors(String[] value)
    {
        this.beforeGetArmPoseInferiors = value;
    }

    public void setOverrideGetArmPoseSuperiors(String[] value)
    {
        this.overrideGetArmPoseSuperiors = value;
    }

    public void setOverrideGetArmPoseInferiors(String[] value)
    {
        this.overrideGetArmPoseInferiors = value;
    }

    public void setAfterGetArmPoseSuperiors(String[] value)
    {
        this.afterGetArmPoseSuperiors = value;
    }

    public void setAfterGetArmPoseInferiors(String[] value)
    {
        this.afterGetArmPoseInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetDeathMaxRotationSuperiors()
    {
        return this.beforeGetDeathMaxRotationSuperiors;
    }

    public String[] getBeforeGetDeathMaxRotationInferiors()
    {
        return this.beforeGetDeathMaxRotationInferiors;
    }

    public String[] getOverrideGetDeathMaxRotationSuperiors()
    {
        return this.overrideGetDeathMaxRotationSuperiors;
    }

    public String[] getOverrideGetDeathMaxRotationInferiors()
    {
        return this.overrideGetDeathMaxRotationInferiors;
    }

    public String[] getAfterGetDeathMaxRotationSuperiors()
    {
        return this.afterGetDeathMaxRotationSuperiors;
    }

    public String[] getAfterGetDeathMaxRotationInferiors()
    {
        return this.afterGetDeathMaxRotationInferiors;
    }

    public void setBeforeGetDeathMaxRotationSuperiors(String[] value)
    {
        this.beforeGetDeathMaxRotationSuperiors = value;
    }

    public void setBeforeGetDeathMaxRotationInferiors(String[] value)
    {
        this.beforeGetDeathMaxRotationInferiors = value;
    }

    public void setOverrideGetDeathMaxRotationSuperiors(String[] value)
    {
        this.overrideGetDeathMaxRotationSuperiors = value;
    }

    public void setOverrideGetDeathMaxRotationInferiors(String[] value)
    {
        this.overrideGetDeathMaxRotationInferiors = value;
    }

    public void setAfterGetDeathMaxRotationSuperiors(String[] value)
    {
        this.afterGetDeathMaxRotationSuperiors = value;
    }

    public void setAfterGetDeathMaxRotationInferiors(String[] value)
    {
        this.afterGetDeathMaxRotationInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetEntityModelSuperiors()
    {
        return this.beforeGetEntityModelSuperiors;
    }

    public String[] getBeforeGetEntityModelInferiors()
    {
        return this.beforeGetEntityModelInferiors;
    }

    public String[] getOverrideGetEntityModelSuperiors()
    {
        return this.overrideGetEntityModelSuperiors;
    }

    public String[] getOverrideGetEntityModelInferiors()
    {
        return this.overrideGetEntityModelInferiors;
    }

    public String[] getAfterGetEntityModelSuperiors()
    {
        return this.afterGetEntityModelSuperiors;
    }

    public String[] getAfterGetEntityModelInferiors()
    {
        return this.afterGetEntityModelInferiors;
    }

    public void setBeforeGetEntityModelSuperiors(String[] value)
    {
        this.beforeGetEntityModelSuperiors = value;
    }

    public void setBeforeGetEntityModelInferiors(String[] value)
    {
        this.beforeGetEntityModelInferiors = value;
    }

    public void setOverrideGetEntityModelSuperiors(String[] value)
    {
        this.overrideGetEntityModelSuperiors = value;
    }

    public void setOverrideGetEntityModelInferiors(String[] value)
    {
        this.overrideGetEntityModelInferiors = value;
    }

    public void setAfterGetEntityModelSuperiors(String[] value)
    {
        this.afterGetEntityModelSuperiors = value;
    }

    public void setAfterGetEntityModelInferiors(String[] value)
    {
        this.afterGetEntityModelInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetEntityTextureSuperiors()
    {
        return this.beforeGetEntityTextureSuperiors;
    }

    public String[] getBeforeGetEntityTextureInferiors()
    {
        return this.beforeGetEntityTextureInferiors;
    }

    public String[] getOverrideGetEntityTextureSuperiors()
    {
        return this.overrideGetEntityTextureSuperiors;
    }

    public String[] getOverrideGetEntityTextureInferiors()
    {
        return this.overrideGetEntityTextureInferiors;
    }

    public String[] getAfterGetEntityTextureSuperiors()
    {
        return this.afterGetEntityTextureSuperiors;
    }

    public String[] getAfterGetEntityTextureInferiors()
    {
        return this.afterGetEntityTextureInferiors;
    }

    public void setBeforeGetEntityTextureSuperiors(String[] value)
    {
        this.beforeGetEntityTextureSuperiors = value;
    }

    public void setBeforeGetEntityTextureInferiors(String[] value)
    {
        this.beforeGetEntityTextureInferiors = value;
    }

    public void setOverrideGetEntityTextureSuperiors(String[] value)
    {
        this.overrideGetEntityTextureSuperiors = value;
    }

    public void setOverrideGetEntityTextureInferiors(String[] value)
    {
        this.overrideGetEntityTextureInferiors = value;
    }

    public void setAfterGetEntityTextureSuperiors(String[] value)
    {
        this.afterGetEntityTextureSuperiors = value;
    }

    public void setAfterGetEntityTextureInferiors(String[] value)
    {
        this.afterGetEntityTextureInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetFontRendererFromRenderManagerSuperiors()
    {
        return this.beforeGetFontRendererFromRenderManagerSuperiors;
    }

    public String[] getBeforeGetFontRendererFromRenderManagerInferiors()
    {
        return this.beforeGetFontRendererFromRenderManagerInferiors;
    }

    public String[] getOverrideGetFontRendererFromRenderManagerSuperiors()
    {
        return this.overrideGetFontRendererFromRenderManagerSuperiors;
    }

    public String[] getOverrideGetFontRendererFromRenderManagerInferiors()
    {
        return this.overrideGetFontRendererFromRenderManagerInferiors;
    }

    public String[] getAfterGetFontRendererFromRenderManagerSuperiors()
    {
        return this.afterGetFontRendererFromRenderManagerSuperiors;
    }

    public String[] getAfterGetFontRendererFromRenderManagerInferiors()
    {
        return this.afterGetFontRendererFromRenderManagerInferiors;
    }

    public void setBeforeGetFontRendererFromRenderManagerSuperiors(String[] value)
    {
        this.beforeGetFontRendererFromRenderManagerSuperiors = value;
    }

    public void setBeforeGetFontRendererFromRenderManagerInferiors(String[] value)
    {
        this.beforeGetFontRendererFromRenderManagerInferiors = value;
    }

    public void setOverrideGetFontRendererFromRenderManagerSuperiors(String[] value)
    {
        this.overrideGetFontRendererFromRenderManagerSuperiors = value;
    }

    public void setOverrideGetFontRendererFromRenderManagerInferiors(String[] value)
    {
        this.overrideGetFontRendererFromRenderManagerInferiors = value;
    }

    public void setAfterGetFontRendererFromRenderManagerSuperiors(String[] value)
    {
        this.afterGetFontRendererFromRenderManagerSuperiors = value;
    }

    public void setAfterGetFontRendererFromRenderManagerInferiors(String[] value)
    {
        this.afterGetFontRendererFromRenderManagerInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetRenderManagerSuperiors()
    {
        return this.beforeGetRenderManagerSuperiors;
    }

    public String[] getBeforeGetRenderManagerInferiors()
    {
        return this.beforeGetRenderManagerInferiors;
    }

    public String[] getOverrideGetRenderManagerSuperiors()
    {
        return this.overrideGetRenderManagerSuperiors;
    }

    public String[] getOverrideGetRenderManagerInferiors()
    {
        return this.overrideGetRenderManagerInferiors;
    }

    public String[] getAfterGetRenderManagerSuperiors()
    {
        return this.afterGetRenderManagerSuperiors;
    }

    public String[] getAfterGetRenderManagerInferiors()
    {
        return this.afterGetRenderManagerInferiors;
    }

    public void setBeforeGetRenderManagerSuperiors(String[] value)
    {
        this.beforeGetRenderManagerSuperiors = value;
    }

    public void setBeforeGetRenderManagerInferiors(String[] value)
    {
        this.beforeGetRenderManagerInferiors = value;
    }

    public void setOverrideGetRenderManagerSuperiors(String[] value)
    {
        this.overrideGetRenderManagerSuperiors = value;
    }

    public void setOverrideGetRenderManagerInferiors(String[] value)
    {
        this.overrideGetRenderManagerInferiors = value;
    }

    public void setAfterGetRenderManagerSuperiors(String[] value)
    {
        this.afterGetRenderManagerSuperiors = value;
    }

    public void setAfterGetRenderManagerInferiors(String[] value)
    {
        this.afterGetRenderManagerInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetRenderOffsetSuperiors()
    {
        return this.beforeGetRenderOffsetSuperiors;
    }

    public String[] getBeforeGetRenderOffsetInferiors()
    {
        return this.beforeGetRenderOffsetInferiors;
    }

    public String[] getOverrideGetRenderOffsetSuperiors()
    {
        return this.overrideGetRenderOffsetSuperiors;
    }

    public String[] getOverrideGetRenderOffsetInferiors()
    {
        return this.overrideGetRenderOffsetInferiors;
    }

    public String[] getAfterGetRenderOffsetSuperiors()
    {
        return this.afterGetRenderOffsetSuperiors;
    }

    public String[] getAfterGetRenderOffsetInferiors()
    {
        return this.afterGetRenderOffsetInferiors;
    }

    public void setBeforeGetRenderOffsetSuperiors(String[] value)
    {
        this.beforeGetRenderOffsetSuperiors = value;
    }

    public void setBeforeGetRenderOffsetInferiors(String[] value)
    {
        this.beforeGetRenderOffsetInferiors = value;
    }

    public void setOverrideGetRenderOffsetSuperiors(String[] value)
    {
        this.overrideGetRenderOffsetSuperiors = value;
    }

    public void setOverrideGetRenderOffsetInferiors(String[] value)
    {
        this.overrideGetRenderOffsetInferiors = value;
    }

    public void setAfterGetRenderOffsetSuperiors(String[] value)
    {
        this.afterGetRenderOffsetSuperiors = value;
    }

    public void setAfterGetRenderOffsetInferiors(String[] value)
    {
        this.afterGetRenderOffsetInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeGetSwingProgressSuperiors()
    {
        return this.beforeGetSwingProgressSuperiors;
    }

    public String[] getBeforeGetSwingProgressInferiors()
    {
        return this.beforeGetSwingProgressInferiors;
    }

    public String[] getOverrideGetSwingProgressSuperiors()
    {
        return this.overrideGetSwingProgressSuperiors;
    }

    public String[] getOverrideGetSwingProgressInferiors()
    {
        return this.overrideGetSwingProgressInferiors;
    }

    public String[] getAfterGetSwingProgressSuperiors()
    {
        return this.afterGetSwingProgressSuperiors;
    }

    public String[] getAfterGetSwingProgressInferiors()
    {
        return this.afterGetSwingProgressInferiors;
    }

    public void setBeforeGetSwingProgressSuperiors(String[] value)
    {
        this.beforeGetSwingProgressSuperiors = value;
    }

    public void setBeforeGetSwingProgressInferiors(String[] value)
    {
        this.beforeGetSwingProgressInferiors = value;
    }

    public void setOverrideGetSwingProgressSuperiors(String[] value)
    {
        this.overrideGetSwingProgressSuperiors = value;
    }

    public void setOverrideGetSwingProgressInferiors(String[] value)
    {
        this.overrideGetSwingProgressInferiors = value;
    }

    public void setAfterGetSwingProgressSuperiors(String[] value)
    {
        this.afterGetSwingProgressSuperiors = value;
    }

    public void setAfterGetSwingProgressInferiors(String[] value)
    {
        this.afterGetSwingProgressInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeHandleRotationFloatSuperiors()
    {
        return this.beforeHandleRotationFloatSuperiors;
    }

    public String[] getBeforeHandleRotationFloatInferiors()
    {
        return this.beforeHandleRotationFloatInferiors;
    }

    public String[] getOverrideHandleRotationFloatSuperiors()
    {
        return this.overrideHandleRotationFloatSuperiors;
    }

    public String[] getOverrideHandleRotationFloatInferiors()
    {
        return this.overrideHandleRotationFloatInferiors;
    }

    public String[] getAfterHandleRotationFloatSuperiors()
    {
        return this.afterHandleRotationFloatSuperiors;
    }

    public String[] getAfterHandleRotationFloatInferiors()
    {
        return this.afterHandleRotationFloatInferiors;
    }

    public void setBeforeHandleRotationFloatSuperiors(String[] value)
    {
        this.beforeHandleRotationFloatSuperiors = value;
    }

    public void setBeforeHandleRotationFloatInferiors(String[] value)
    {
        this.beforeHandleRotationFloatInferiors = value;
    }

    public void setOverrideHandleRotationFloatSuperiors(String[] value)
    {
        this.overrideHandleRotationFloatSuperiors = value;
    }

    public void setOverrideHandleRotationFloatInferiors(String[] value)
    {
        this.overrideHandleRotationFloatInferiors = value;
    }

    public void setAfterHandleRotationFloatSuperiors(String[] value)
    {
        this.afterHandleRotationFloatSuperiors = value;
    }

    public void setAfterHandleRotationFloatInferiors(String[] value)
    {
        this.afterHandleRotationFloatInferiors = value;
    }

    // ############################################################################

    public String[] getBeforePreRenderCallbackSuperiors()
    {
        return this.beforePreRenderCallbackSuperiors;
    }

    public String[] getBeforePreRenderCallbackInferiors()
    {
        return this.beforePreRenderCallbackInferiors;
    }

    public String[] getOverridePreRenderCallbackSuperiors()
    {
        return this.overridePreRenderCallbackSuperiors;
    }

    public String[] getOverridePreRenderCallbackInferiors()
    {
        return this.overridePreRenderCallbackInferiors;
    }

    public String[] getAfterPreRenderCallbackSuperiors()
    {
        return this.afterPreRenderCallbackSuperiors;
    }

    public String[] getAfterPreRenderCallbackInferiors()
    {
        return this.afterPreRenderCallbackInferiors;
    }

    public void setBeforePreRenderCallbackSuperiors(String[] value)
    {
        this.beforePreRenderCallbackSuperiors = value;
    }

    public void setBeforePreRenderCallbackInferiors(String[] value)
    {
        this.beforePreRenderCallbackInferiors = value;
    }

    public void setOverridePreRenderCallbackSuperiors(String[] value)
    {
        this.overridePreRenderCallbackSuperiors = value;
    }

    public void setOverridePreRenderCallbackInferiors(String[] value)
    {
        this.overridePreRenderCallbackInferiors = value;
    }

    public void setAfterPreRenderCallbackSuperiors(String[] value)
    {
        this.afterPreRenderCallbackSuperiors = value;
    }

    public void setAfterPreRenderCallbackInferiors(String[] value)
    {
        this.afterPreRenderCallbackInferiors = value;
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

    public String[] getBeforeRenderItemSuperiors()
    {
        return this.beforeRenderItemSuperiors;
    }

    public String[] getBeforeRenderItemInferiors()
    {
        return this.beforeRenderItemInferiors;
    }

    public String[] getOverrideRenderItemSuperiors()
    {
        return this.overrideRenderItemSuperiors;
    }

    public String[] getOverrideRenderItemInferiors()
    {
        return this.overrideRenderItemInferiors;
    }

    public String[] getAfterRenderItemSuperiors()
    {
        return this.afterRenderItemSuperiors;
    }

    public String[] getAfterRenderItemInferiors()
    {
        return this.afterRenderItemInferiors;
    }

    public void setBeforeRenderItemSuperiors(String[] value)
    {
        this.beforeRenderItemSuperiors = value;
    }

    public void setBeforeRenderItemInferiors(String[] value)
    {
        this.beforeRenderItemInferiors = value;
    }

    public void setOverrideRenderItemSuperiors(String[] value)
    {
        this.overrideRenderItemSuperiors = value;
    }

    public void setOverrideRenderItemInferiors(String[] value)
    {
        this.overrideRenderItemInferiors = value;
    }

    public void setAfterRenderItemSuperiors(String[] value)
    {
        this.afterRenderItemSuperiors = value;
    }

    public void setAfterRenderItemInferiors(String[] value)
    {
        this.afterRenderItemInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeRenderLeftArmSuperiors()
    {
        return this.beforeRenderLeftArmSuperiors;
    }

    public String[] getBeforeRenderLeftArmInferiors()
    {
        return this.beforeRenderLeftArmInferiors;
    }

    public String[] getOverrideRenderLeftArmSuperiors()
    {
        return this.overrideRenderLeftArmSuperiors;
    }

    public String[] getOverrideRenderLeftArmInferiors()
    {
        return this.overrideRenderLeftArmInferiors;
    }

    public String[] getAfterRenderLeftArmSuperiors()
    {
        return this.afterRenderLeftArmSuperiors;
    }

    public String[] getAfterRenderLeftArmInferiors()
    {
        return this.afterRenderLeftArmInferiors;
    }

    public void setBeforeRenderLeftArmSuperiors(String[] value)
    {
        this.beforeRenderLeftArmSuperiors = value;
    }

    public void setBeforeRenderLeftArmInferiors(String[] value)
    {
        this.beforeRenderLeftArmInferiors = value;
    }

    public void setOverrideRenderLeftArmSuperiors(String[] value)
    {
        this.overrideRenderLeftArmSuperiors = value;
    }

    public void setOverrideRenderLeftArmInferiors(String[] value)
    {
        this.overrideRenderLeftArmInferiors = value;
    }

    public void setAfterRenderLeftArmSuperiors(String[] value)
    {
        this.afterRenderLeftArmSuperiors = value;
    }

    public void setAfterRenderLeftArmInferiors(String[] value)
    {
        this.afterRenderLeftArmInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeRenderNameSuperiors()
    {
        return this.beforeRenderNameSuperiors;
    }

    public String[] getBeforeRenderNameInferiors()
    {
        return this.beforeRenderNameInferiors;
    }

    public String[] getOverrideRenderNameSuperiors()
    {
        return this.overrideRenderNameSuperiors;
    }

    public String[] getOverrideRenderNameInferiors()
    {
        return this.overrideRenderNameInferiors;
    }

    public String[] getAfterRenderNameSuperiors()
    {
        return this.afterRenderNameSuperiors;
    }

    public String[] getAfterRenderNameInferiors()
    {
        return this.afterRenderNameInferiors;
    }

    public void setBeforeRenderNameSuperiors(String[] value)
    {
        this.beforeRenderNameSuperiors = value;
    }

    public void setBeforeRenderNameInferiors(String[] value)
    {
        this.beforeRenderNameInferiors = value;
    }

    public void setOverrideRenderNameSuperiors(String[] value)
    {
        this.overrideRenderNameSuperiors = value;
    }

    public void setOverrideRenderNameInferiors(String[] value)
    {
        this.overrideRenderNameInferiors = value;
    }

    public void setAfterRenderNameSuperiors(String[] value)
    {
        this.afterRenderNameSuperiors = value;
    }

    public void setAfterRenderNameInferiors(String[] value)
    {
        this.afterRenderNameInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeRenderRightArmSuperiors()
    {
        return this.beforeRenderRightArmSuperiors;
    }

    public String[] getBeforeRenderRightArmInferiors()
    {
        return this.beforeRenderRightArmInferiors;
    }

    public String[] getOverrideRenderRightArmSuperiors()
    {
        return this.overrideRenderRightArmSuperiors;
    }

    public String[] getOverrideRenderRightArmInferiors()
    {
        return this.overrideRenderRightArmInferiors;
    }

    public String[] getAfterRenderRightArmSuperiors()
    {
        return this.afterRenderRightArmSuperiors;
    }

    public String[] getAfterRenderRightArmInferiors()
    {
        return this.afterRenderRightArmInferiors;
    }

    public void setBeforeRenderRightArmSuperiors(String[] value)
    {
        this.beforeRenderRightArmSuperiors = value;
    }

    public void setBeforeRenderRightArmInferiors(String[] value)
    {
        this.beforeRenderRightArmInferiors = value;
    }

    public void setOverrideRenderRightArmSuperiors(String[] value)
    {
        this.overrideRenderRightArmSuperiors = value;
    }

    public void setOverrideRenderRightArmInferiors(String[] value)
    {
        this.overrideRenderRightArmInferiors = value;
    }

    public void setAfterRenderRightArmSuperiors(String[] value)
    {
        this.afterRenderRightArmSuperiors = value;
    }

    public void setAfterRenderRightArmInferiors(String[] value)
    {
        this.afterRenderRightArmInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeSetModelVisibilitiesSuperiors()
    {
        return this.beforeSetModelVisibilitiesSuperiors;
    }

    public String[] getBeforeSetModelVisibilitiesInferiors()
    {
        return this.beforeSetModelVisibilitiesInferiors;
    }

    public String[] getOverrideSetModelVisibilitiesSuperiors()
    {
        return this.overrideSetModelVisibilitiesSuperiors;
    }

    public String[] getOverrideSetModelVisibilitiesInferiors()
    {
        return this.overrideSetModelVisibilitiesInferiors;
    }

    public String[] getAfterSetModelVisibilitiesSuperiors()
    {
        return this.afterSetModelVisibilitiesSuperiors;
    }

    public String[] getAfterSetModelVisibilitiesInferiors()
    {
        return this.afterSetModelVisibilitiesInferiors;
    }

    public void setBeforeSetModelVisibilitiesSuperiors(String[] value)
    {
        this.beforeSetModelVisibilitiesSuperiors = value;
    }

    public void setBeforeSetModelVisibilitiesInferiors(String[] value)
    {
        this.beforeSetModelVisibilitiesInferiors = value;
    }

    public void setOverrideSetModelVisibilitiesSuperiors(String[] value)
    {
        this.overrideSetModelVisibilitiesSuperiors = value;
    }

    public void setOverrideSetModelVisibilitiesInferiors(String[] value)
    {
        this.overrideSetModelVisibilitiesInferiors = value;
    }

    public void setAfterSetModelVisibilitiesSuperiors(String[] value)
    {
        this.afterSetModelVisibilitiesSuperiors = value;
    }

    public void setAfterSetModelVisibilitiesInferiors(String[] value)
    {
        this.afterSetModelVisibilitiesInferiors = value;
    }

    // ############################################################################

    public String[] getBeforeShouldRenderSuperiors()
    {
        return this.beforeShouldRenderSuperiors;
    }

    public String[] getBeforeShouldRenderInferiors()
    {
        return this.beforeShouldRenderInferiors;
    }

    public String[] getOverrideShouldRenderSuperiors()
    {
        return this.overrideShouldRenderSuperiors;
    }

    public String[] getOverrideShouldRenderInferiors()
    {
        return this.overrideShouldRenderInferiors;
    }

    public String[] getAfterShouldRenderSuperiors()
    {
        return this.afterShouldRenderSuperiors;
    }

    public String[] getAfterShouldRenderInferiors()
    {
        return this.afterShouldRenderInferiors;
    }

    public void setBeforeShouldRenderSuperiors(String[] value)
    {
        this.beforeShouldRenderSuperiors = value;
    }

    public void setBeforeShouldRenderInferiors(String[] value)
    {
        this.beforeShouldRenderInferiors = value;
    }

    public void setOverrideShouldRenderSuperiors(String[] value)
    {
        this.overrideShouldRenderSuperiors = value;
    }

    public void setOverrideShouldRenderInferiors(String[] value)
    {
        this.overrideShouldRenderInferiors = value;
    }

    public void setAfterShouldRenderSuperiors(String[] value)
    {
        this.afterShouldRenderSuperiors = value;
    }

    public void setAfterShouldRenderInferiors(String[] value)
    {
        this.afterShouldRenderInferiors = value;
    }
}