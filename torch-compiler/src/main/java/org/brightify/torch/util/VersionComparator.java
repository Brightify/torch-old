package org.brightify.torch.util;

import java.util.Comparator;

/**
 * @author <a href="mailto:tadeas.kriz@brainwashstudio.com">Tadeas Kriz</a>
 */
public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(String v1, String v2) {
        String[] version1Split = v1.split("\\.");
        String[] version2Split = v2.split("\\.");

        int count = version1Split.length > version2Split.length ? version1Split.length : version2Split.length;

        for(int i = 0; i < count; i++) {
            int version1Part = i < version1Split.length ? Integer.parseInt(version1Split[i]) : 0;
            int version2Part = i < version2Split.length ? Integer.parseInt(version2Split[i]) : 0;

            int compared = Integer.compare(version1Part, version2Part);
            if(compared != 0) {
                return compared;
            }
        }

        return 0;
    }

}
