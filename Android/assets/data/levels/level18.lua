wrongButonsArray = {}

function addObjects()
    world:setPressingAction(PICKING_BODIES);
    ------------------
    ball_0 = luajava.new(Ball)
    ball_0:setPosition(702.0, 200.99995)
    ball_0:setRotation(0.0)
    ball_0:setBodyType(DYNAMIC_BODY)
    ball_0:setName('ball_0')
    ball_0:setVisible(true)
    world:add(ball_0)

    ------------------
    trigger_1 = luajava.new(Trigger)
    trigger_1:setPosition(624.0, 163.99998)
    trigger_1:setRotation(0.0)
    trigger_1:setBodyType(STATIC_BODY)
    trigger_1:setName('trigger_1')
    trigger_1:setVisible(true)
    world:add(trigger_1)

    ------------------
    trigger_2 = luajava.new(Trigger)
    trigger_2:setPosition(692.0, 291.0)
    trigger_2:setRotation(-174.0)
    trigger_2:setBodyType(STATIC_BODY)
    trigger_2:setName('trigger_2')
    trigger_2:setVisible(true)
    world:add(trigger_2)

    ------------------
    trigger_3 = luajava.new(Trigger)
    trigger_3:setPosition(557.99994, 299.99997)
    trigger_3:setRotation(-91.0)
    trigger_3:setBodyType(STATIC_BODY)
    trigger_3:setName('trigger_3')
    trigger_3:setVisible(true)
    world:add(trigger_3)

    ------------------
    trigger_4 = luajava.new(Trigger)
    trigger_4:setPosition(648.0, 423.99997)
    trigger_4:setRotation(162.0)
    trigger_4:setBodyType(STATIC_BODY)
    trigger_4:setName('trigger_4')
    trigger_4:setVisible(true)
    world:add(trigger_4)

    ------------------
    trigger_5 = luajava.new(Trigger)
    trigger_5:setPosition(511.0, 434.0)
    trigger_5:setRotation(-174.0)
    trigger_5:setBodyType(STATIC_BODY)
    trigger_5:setName('trigger_5')
    trigger_5:setVisible(true)
    world:add(trigger_5)

    ------------------
    trigger_6 = luajava.new(Trigger)
    trigger_6:setPosition(349.99997, 262.0)
    trigger_6:setRotation(2.0)
    trigger_6:setBodyType(STATIC_BODY)
    trigger_6:setName('trigger_6')
    trigger_6:setVisible(true)
    world:add(trigger_6)

    ------------------
    emitter_7 = luajava.new(Emitter)
    emitter_7:setPosition(435.99994, 412.9999)
    emitter_7:setRotation(-91.0)
    emitter_7:setBodyType(STATIC_BODY)
    emitter_7:setName('emitter_7')
    emitter_7:setVisible(true)
    emitter_7:setHasControl(false)
    emitter_7:setAutoFire(true)
    world:add(emitter_7)

    ------------------
    trigger_8 = luajava.new(Trigger)
    trigger_8:setPosition(346.0, 392.0)
    trigger_8:setRotation(-167.0)
    trigger_8:setBodyType(STATIC_BODY)
    trigger_8:setName('trigger_8')
    trigger_8:setVisible(true)
    world:add(trigger_8)

    ------------------
    trigger_9 = luajava.new(Trigger)
    trigger_9:setPosition(259.99997, 221.99998)
    trigger_9:setRotation(18.0)
    trigger_9:setBodyType(STATIC_BODY)
    trigger_9:setName('trigger_9')
    trigger_9:setVisible(true)
    world:add(trigger_9)

    ------------------
    trigger_10 = luajava.new(Trigger)
    trigger_10:setPosition(227.99998, 372.0)
    trigger_10:setRotation(-180.0)
    trigger_10:setBodyType(STATIC_BODY)
    trigger_10:setName('trigger_10')
    trigger_10:setVisible(true)
    world:add(trigger_10)

    ------------------
    trigger_11 = luajava.new(Trigger)
    trigger_11:setPosition(68.0, 256.99997)
    trigger_11:setRotation(-89.0)
    trigger_11:setBodyType(STATIC_BODY)
    trigger_11:setName('trigger_11')
    trigger_11:setVisible(true)
    world:add(trigger_11)

    ------------------
    trigger_12 = luajava.new(Trigger)
    trigger_12:setPosition(186.0, 140.99997)
    trigger_12:setRotation(67.0)
    trigger_12:setBodyType(STATIC_BODY)
    trigger_12:setName('trigger_12')
    trigger_12:setVisible(true)
    world:add(trigger_12)

    ------------------
    trigger_13 = luajava.new(Trigger)
    trigger_13:setPosition(515.0, 157.99998)
    trigger_13:setRotation(0.0)
    trigger_13:setBodyType(STATIC_BODY)
    trigger_13:setName('trigger_13')
    trigger_13:setVisible(true)
    world:add(trigger_13)

    ------------------
    Ground_14 = luajava.new(Ground)
    Ground_14:addVertex(43.955708, 177.58173)
    Ground_14:addVertex(-3.516674E-4, 172.307)
    Ground_14:addVertex(8.790879, 3.5157914)
    Ground_14:addVertex(152.9667, -7.033348E-4)
    Ground_14:addVertex(159.99965, 42.197098)
    Ground_14:addVertex(119.56009, 31.647661)
    Ground_14:addVertex(29.889757, 35.16414)
    Ground_14:addToPosition(3.5165105, 7.03296)
    Ground_14:setRotation(0.0)
    Ground_14:setBodyType(STATIC_BODY)
    Ground_14:setName('Ground_14')
    Ground_14:setVisible(true)
    world:add(Ground_14)

    ------------------
    Ground_15 = luajava.new(Ground)
    Ground_15:addVertex(144.17496, 281.315)
    Ground_15:addVertex(135.38374, 337.57874)
    Ground_15:addVertex(210.98816, 360.4359)
    Ground_15:addVertex(239.12001, 311.2051)
    Ground_15:addVertex(272.5266, 374.50183)
    Ground_15:addVertex(516.9222, 413.18317)
    Ground_15:addVertex(530.98816, 358.67767)
    Ground_15:addVertex(601.3178, 411.42493)
    Ground_15:addVertex(682.19696, 381.53482)
    Ground_15:addVertex(638.2409, 291.86447)
    Ground_15:addVertex(703.2958, 276.0403)
    Ground_15:addVertex(705.054, 182.85349)
    Ground_15:addVertex(727.9112, 138.8974)
    Ground_15:addVertex(713.8453, 101.97434)
    Ground_15:addVertex(655.8233, 100.2161)
    Ground_15:addVertex(629.44965, 110.765564)
    Ground_15:addVertex(627.6914, 50.985325)
    Ground_15:addVertex(736.70245, -0.0036926267)
    Ground_15:addVertex(756.043, 407.90845)
    Ground_15:addVertex(571.4277, 436.0403)
    Ground_15:addVertex(121.317825, 411.42493)
    Ground_15:addVertex(-8.792877E-4, 383.29306)
    Ground_15:addVertex(3.5156317, 226.80952)
    Ground_15:addToPosition(7.79123, 36.92307)
    Ground_15:setRotation(0.0)
    Ground_15:setBodyType(STATIC_BODY)
    Ground_15:setName('Ground_15')
    Ground_15:setVisible(true)
    world:add(Ground_15)

    ------------------
    Ground_16 = luajava.new(Ground)
    Ground_16:addVertex(114.26196, 189.88293)
    Ground_16:addVertex(117.778465, 110.762024)
    Ground_16:addVertex(68.5477, 59.77302)
    Ground_16:addVertex(-0.023742676, 29.882902)
    Ground_16:addVertex(61.51474, -0.007209778)
    Ground_16:addVertex(133.60263, 73.83894)
    Ground_16:addToPosition(237.36267, 72.08791)
    Ground_16:setRotation(0.0)
    Ground_16:setBodyType(STATIC_BODY)
    Ground_16:setName('Ground_16')
    Ground_16:setVisible(true)
    world:add(Ground_16)

    ------------------
    Ground_17 = luajava.new(Ground)
    Ground_17:addVertex(19.298248, 218.01813)
    Ground_17:addVertex(-0.042388916, 105.49062)
    Ground_17:addVertex(21.056488, 12.303814)
    Ground_17:addVertex(212.70477, -0.0038681028)
    Ground_17:addVertex(124.79272, 52.743374)
    Ground_17:addVertex(114.24328, 182.85329)
    Ground_17:addVertex(91.3861, 28.127987)
    Ground_17:addVertex(21.056488, 49.22689)
    Ground_17:addVertex(19.298248, 128.34778)
    Ground_17:addVertex(29.847687, 223.29285)
    Ground_17:addToPosition(423.73633, 38.681324)
    Ground_17:setRotation(0.0)
    Ground_17:setBodyType(STATIC_BODY)
    Ground_17:setName('Ground_17')
    Ground_17:setVisible(true)
    world:add(Ground_17)

    world:addInteractType(BALL)

    table.insert(wrongButonsArray, trigger_1)
    table.insert(wrongButonsArray, trigger_2)
    table.insert(wrongButonsArray, trigger_3)
    table.insert(wrongButonsArray, trigger_4)
    table.insert(wrongButonsArray, trigger_5)
    table.insert(wrongButonsArray, trigger_6)
    table.insert(wrongButonsArray, trigger_7)
    table.insert(wrongButonsArray, trigger_8)
    table.insert(wrongButonsArray, trigger_9)
    table.insert(wrongButonsArray, trigger_11)
    table.insert(wrongButonsArray, trigger_12)
    table.insert(wrongButonsArray, trigger_13)
end
function onCreate()
    addObjects()
end
function onCheck()
    for i = 1, #wrongButonsArray do
        if wrongButonsArray[i]:getState() then
            return false
        end
    end
    
    return trigger_10:getState()
end