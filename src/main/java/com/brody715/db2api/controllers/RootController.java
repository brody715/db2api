package com.brody715.db2api.controllers;

import com.brody715.db2api.common.BaseContext;
import com.brody715.db2api.common.R;
import com.brody715.db2api.model.ExecuteRequest;
import com.brody715.db2api.model.api.OperationNode;
import com.brody715.db2api.service.RootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @Autowired
    RootService rootService;

    @PostMapping("/db2api")
    public R<Object> db2api(@RequestBody OperationNode op) throws Exception {
        var input = new ExecuteRequest();
        input.setOp(op);
        input.setUser(BaseContext.instance().getUsername());

        var resp = rootService.execute(input);
        R<Object> r = R.success(resp.data);
        r.add("debug", resp.debug);
        return r;
    }
}
