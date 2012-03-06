/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package actions.deadbolt;

import controllers.deadbolt.DeadboltHandler;
import models.deadbolt.RoleHolder;
import play.mvc.Http;
import play.mvc.Result;

/**
 * @author Steve Chaloner
 */
public class RestrictAction extends AbstractDeadboltAction<Restrict>
{
    @Override
    public Result call(Http.Context ctx) throws Throwable
    {
        Result result;
        if (isActionAuthorised(ctx))
        {
            result = delegate.call(ctx);
        }
        else
        {
            DeadboltHandler deadboltHandler = getDeadboltHandler(configuration.handler());
            deadboltHandler.beforeRoleCheck();

            if (isAllowed(ctx,
                          deadboltHandler))
            {
                markActionAsAuthorised(ctx);
                result = delegate.call(ctx);
            }
            else
            {
                markActionAsUnauthorised(ctx);
                result = onAccessFailure(deadboltHandler,
                                         configuration.content(),
                                         ctx);
            }
        }

        return result;
    }
    
    private boolean isAllowed(Http.Context ctx,
                              DeadboltHandler deadboltHandler)
    {
        RoleHolder roleHolder = getRoleHolder(ctx,
                                              deadboltHandler);

        boolean roleOk = false;
        if (roleHolder != null)
        {
            roleOk = checkRole(roleHolder,
                               configuration.roles());
        }

        return roleOk;
    }
}