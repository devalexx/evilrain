function onCreate(world)
    world:setWinHint('Select top left!!!')

    ground = luajava.newInstance(Ground)
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

    ground = luajava.newInstance(Ground)
    ground:addVertex(528, 248)
    ground:addVertex(200, 248)
    ground:addVertex(200, 240)
    ground:addVertex(528, 240)
    world:add(ground)
    ground = luajava.newInstance(Ground)
    ground:addVertex(528, 148)
    ground:addVertex(200, 148)
    ground:addVertex(200, 140)
    ground:addVertex(528, 140)
    world:add(ground)

    ground = luajava.newInstance(Ground)
    ground:addVertex(304, 64)
    ground:addVertex(304, 336)
    ground:addVertex(288, 336)
    ground:addVertex(288, 64)
    world:add(ground)
    ground = luajava.newInstance(Ground)
    ground:addVertex(404, 64)
    ground:addVertex(404, 336)
    ground:addVertex(388, 336)
    ground:addVertex(388, 64)
    world:add(ground)
end

function onCheck(mArray)
    has5 = 0
    hasElse = false

    mArray:size()
    for i = 0, mArray:size() - 1 do
        if mArray:get(i):getStringType() == "DROP" then
            if mArray:get(i):getPosition().x < 303 and mArray:get(i):getPosition().y > 241
                    and mArray:get(i):getPosition().x > 148 and mArray:get(i):getPosition().y < 388  then
                has5 = has5 + 1
            else
                hasElse = true
            end
        end
    end

    if has5 > 50 and hasElse == false then
        return true
    else
        return false
    end
end