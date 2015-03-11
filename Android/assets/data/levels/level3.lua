function onCreate(world)
    world:setWinHint('Select top left!!!')
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

function onCheck(dropsPosArray, dropsCount)
    for i = 0, dropsCount - 1 do
        local x = Array:get(dropsPosArray, i * 2)
        local y = Array:get(dropsPosArray, i * 2 + 1)
        if x > 303 or x < 148 or y > 388 or y < 241 then
            return false
        end
    end

    if dropsCount > 10 then
        return true
    else
        return false
    end
end