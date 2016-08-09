//
// $Id$
//

//
// Sphinx UDF function example
//
// Linux
// gcc -fPIC -shared -o readleafletpreview.so readleafletpreview.c
// CREATE FUNCTION sequence RETURNS INT SONAME 'udfexample.so';
// CREATE FUNCTION strtoint RETURNS INT SONAME 'udfexample.so';
// CREATE FUNCTION avgmva RETURNS FLOAT SONAME 'udfexample.so';
//
//

#include "sphinxudf.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#ifdef _MSC_VER
#define snprintf _snprintf
#define DLLEXPORT __declspec(dllexport)
#else
#define DLLEXPORT
#endif

// Max allowed preview leaflet size
#define MAX_PREVIEW_FILE_SIZE (1024*16)

/// UDF version control
/// gets called once when the library is loaded
DLLEXPORT int _read_file_ver ()
{
    return SPH_UDF_VERSION;
}

/// UDF re-initialization func
/// gets called on sighup (workers=prefork only)
DLLEXPORT void _read_file_reinit ()
{
}

/// UDF initialization
/// gets called on every query, when query begins
/// args are filled with values for a particular query
DLLEXPORT int _read_file_init ( SPH_UDF_INIT * init, SPH_UDF_ARGS * args, char *
error_message )
{
    int i;
    if ( args->arg_count != 1)
    {
        snprintf ( error_message, SPH_UDF_ERROR_LEN, "read_file() wrong number of arguments" );
        return 1;
    }
    for (i=0;i<args->arg_count;i++)
    {
        if (args->arg_types[i]!=SPH_UDF_TYPE_STRING )
        {
            snprintf ( error_message, SPH_UDF_ERROR_LEN, "read_file() requires string arguments" );
            return 1;
        }
    }
    return 0;
}

/// UDF deinitialization
/// gets called on every query, when query ends
DLLEXPORT void _read_file_deinit ( SPH_UDF_INIT * init )
{
    // deallocate storage
    // if ( init->func_data )
    // {
    // args->fn_free ( init->func_data );
    // init->func_data = NULL;
    // }
}


/// UDF implementation
/// gets called for every row, unless optimized away
DLLEXPORT char* _read_file ( SPH_UDF_INIT * init, SPH_UDF_ARGS * args, char *
error_flag )
{
    char * res;
    size_t n_read;
    long lSize;

    // check if PIL and SPC files exist
    FILE *fp = fopen(args->arg_values[0],"rb");

    // obtain file size:
    fseek (fp , 0 , SEEK_END);
    lSize = ftell (fp);
    rewind (fp);

    // To keep things small do not load more than a fixed size of data
    if (lSize > MAX_PREVIEW_FILE_SIZE)
        lSize = MAX_PREVIEW_FILE_SIZE;

    // allocate mem storage
    res = (char*) args->fn_malloc(sizeof(char)*lSize);
    if (res == NULL) {
        return (char*)NULL;
    }
    memset(res, 0, lSize);

    /*
    * Read the contents of a file into a buffer. Return the size of the file
      * and set buf to point to a buffer allocated with malloc that contains
      * the file contents.
      */
    n_read = fread (res, 1, lSize, fp);
    if (n_read != lSize) {
        return (char*)NULL;
    }

    /* the whole file is now loaded in the memory buffer. */

    // terminate
    fclose (fp);

    return res;
}
