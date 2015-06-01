# Building RMBench from source #

This page is a first attempt at describing the build process.


# Details #

The RMBench build system is currently not in a good shape, as it targets only eclipse 3.3 and my particular environment. This is partly due to the imperfect PDE tools, which, at least at the time when I last studied them, were generating build files with hard-coded filesystem paths. All this may have changed in the meantime, but I havent had time to investigate. Therefore, our build system is the first item on the TODO list.

Further investigation into the changes that come with version 3.5 (Galileo) show that we are faced with quite a mess, created by the new P2 system, which will have to be sorted out slowly.