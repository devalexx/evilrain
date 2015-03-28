function onCreate()
    world:setKeepDropsForever(true)
    world:setPressingAction(1);

    local ground = luajava.new(Ground)
    ground:addVertex(528, 336)
    ground:addVertex(200, 336)
    ground:addVertex(200, 64)
    ground:addVertex(528, 64)
    ground:addVertex(528, 328)
    ground:addVertex(520, 328)
    ground:addVertex(520, 72)
    ground:addVertex(208, 72)
    ground:addVertex(208, 328)
    ground:addVertex(528, 328)
    world:add(ground)

    ground = luajava.new(Ground)
    ground:addVertex(528, 248)
    ground:addVertex(200, 248)
    ground:addVertex(200, 240)
    ground:addVertex(528, 240)
    world:add(ground)
    ground = luajava.new(Ground)
    ground:addVertex(528, 148)
    ground:addVertex(200, 148)
    ground:addVertex(200, 140)
    ground:addVertex(528, 140)
    world:add(ground)

    ground = luajava.new(Ground)
    ground:addVertex(304, 64)
    ground:addVertex(304, 336)
    ground:addVertex(288, 336)
    ground:addVertex(288, 64)
    world:add(ground)
    ground = luajava.new(Ground)
    ground:addVertex(404, 64)
    ground:addVertex(404, 336)
    ground:addVertex(388, 336)
    ground:addVertex(388, 64)
    world:add(ground)
end

function onCheck()
    local count = world:dropsInRect(148, 241, 155, 147)

    if count > 10 and count == world:getDropsCount() then
        return true
    else
        return false
    end
end