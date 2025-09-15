package org.servera.pkg;

import java.util.ArrayList;
import java.util.List;

public class PackageStack
{
    protected List<Package> list = new ArrayList<>();

    public PackageStack()
    {}

    public PackageStack(List<Package> list)
    {
        this.list = list;
    }
}
