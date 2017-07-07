package ge.ideadesigngroup.ideamap;

import org.oscim.backend.AssetAdapter;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.XmlRenderThemeMenuCallback;

import java.io.InputStream;

/**
 * Created by IDGUser on 3/21/2017.
 */

public class IdeaMapThemes implements ThemeFile {
    private final String mPath;

    IdeaMapThemes(String path) {
        mPath = path;
    }

    @Override
    public XmlRenderThemeMenuCallback getMenuCallback() {
        return null;
    }

    @Override
    public String getRelativePathPrefix() {
        return "";
    }

    @Override
    public InputStream getRenderThemeAsStream() throws IRenderTheme.ThemeException {
        return AssetAdapter.readFileAsStream(mPath);
    }
}
