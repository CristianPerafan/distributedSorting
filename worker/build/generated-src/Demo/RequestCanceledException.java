//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.10
//
// <auto-generated>
//
// Generated from file `Printer.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Demo;

public class RequestCanceledException extends com.zeroc.Ice.UserException
{
    public RequestCanceledException()
    {
    }

    public RequestCanceledException(Throwable cause)
    {
        super(cause);
    }

    public String ice_id()
    {
        return "::Demo::RequestCanceledException";
    }

    /** @hidden */
    @Override
    protected void _writeImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice("::Demo::RequestCanceledException", -1, true);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _readImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        istr_.endSlice();
    }

    /** @hidden */
    public static final long serialVersionUID = -1672116987L;
}
